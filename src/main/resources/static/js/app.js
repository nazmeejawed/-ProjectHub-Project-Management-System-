/* ===================================================================
   Project Manager — Client-Side Interactions
   =================================================================== */

document.addEventListener('DOMContentLoaded', function () {

    // ── Dark Mode Toggle ─────────────────────────────────────────
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');
    const themeText = document.getElementById('themeText');
    const root = document.documentElement;

    const sunIcon = '<circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4"/>';
    const moonIcon = '<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>';

    // Check LocalStorage on load
    const currentTheme = localStorage.getItem('theme') || 'light';
    if (currentTheme === 'dark') {
        root.setAttribute('data-theme', 'dark');
        if (themeIcon) themeIcon.innerHTML = moonIcon;
        if (themeText) themeText.textContent = 'Dark Mode';
    }

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', function () {
            const isDark = root.getAttribute('data-theme') === 'dark';
            if (isDark) {
                root.removeAttribute('data-theme');
                localStorage.setItem('theme', 'light');
                themeIcon.innerHTML = sunIcon;
                themeText.textContent = 'Light Mode';
            } else {
                root.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
                themeIcon.innerHTML = moonIcon;
                themeText.textContent = 'Dark Mode';
            }
        });
    }

    // ── Sidebar toggle (mobile) ──────────────────────────────────
    const menuToggle = document.querySelector('.menu-toggle');
    const sidebar = document.querySelector('.sidebar');

    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function () {
            sidebar.classList.toggle('open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function (e) {
            if (window.innerWidth <= 768 && sidebar.classList.contains('open')) {
                if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                    sidebar.classList.remove('open');
                }
            }
        });
    }

    // ── Delete confirmation ──────────────────────────────────────
    const deleteForms = document.querySelectorAll('.delete-form');
    deleteForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            showConfirmDialog(
                'Confirm Delete',
                'Are you sure you want to delete this item? This action cannot be undone.',
                function () {
                    form.submit();
                }
            );
        });
    });

    // ── Quick status change ──────────────────────────────────────
    const statusSelects = document.querySelectorAll('.status-select');
    statusSelects.forEach(function (select) {
        select.addEventListener('change', function () {
            const form = this.closest('form');
            if (form) form.submit();
        });
    });

    // ── Auto-dismiss alerts ──────────────────────────────────────
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-8px)';
            setTimeout(function () {
                alert.remove();
            }, 300);
        }, 4000);
    });

    // ── Dashboard: Task distribution bars ────────────────────────
    const chartBars = document.querySelectorAll('.chart-bar-fill');
    setTimeout(function () {
        chartBars.forEach(function (bar) {
            const width = bar.getAttribute('data-width');
            if (width) {
                bar.style.width = width + '%';
            }
        });
    }, 200);

    // ── Dashboard: Animate stat numbers ──────────────────────────
    const statNumbers = document.querySelectorAll('.stat-number');
    statNumbers.forEach(function (el) {
        const target = parseInt(el.textContent, 10);
        if (isNaN(target) || target === 0) return;

        el.textContent = '0';
        let current = 0;
        const increment = Math.max(1, Math.ceil(target / 30));
        const timer = setInterval(function () {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            el.textContent = current;
        }, 30);
    });

    // ── Progress bars animation ──────────────────────────────────
    const progressBars = document.querySelectorAll('.progress-bar-fill');
    setTimeout(function () {
        progressBars.forEach(function (bar) {
            const width = bar.getAttribute('data-progress');
            if (width) {
                bar.style.width = width + '%';
                if (parseInt(width) === 100) {
                    bar.classList.add('complete');
                }
            }
        });
    }, 300);

    // ── Star toggle ──────────────────────────────────────────────
    const starToggle = document.getElementById('starToggle');
    if (starToggle) {
        var starred = localStorage.getItem('dashboard-starred') === 'true';
        if (starred) starToggle.textContent = '★';

        starToggle.addEventListener('click', function () {
            starred = !starred;
            starToggle.textContent = starred ? '★' : '☆';
            starToggle.style.color = starred ? '#f59e0b' : '';
            localStorage.setItem('dashboard-starred', starred);
        });
        if (starred) starToggle.style.color = '#f59e0b';
    }

    // ══════════════════════════════════════════════════════════════
    //  ADD WIDGET SYSTEM
    // ══════════════════════════════════════════════════════════════

    var widgetModal = document.getElementById('widgetModalOverlay');
    var addWidgetBtn = document.getElementById('addWidgetBtn');
    var widgetModalClose = document.getElementById('widgetModalClose');
    var widgetModalCancel = document.getElementById('widgetModalCancel');
    var widgetModalApply = document.getElementById('widgetModalApply');

    if (!widgetModal || !addWidgetBtn) return; // Not on dashboard page

    // Default visible widgets
    var defaultWidgets = [
        'stats-row',
        'task-distribution',
        'upcoming-deadlines',
        'overdue-tasks',
        'project-status',
        'recent-projects'
    ];

    // All available widget IDs
    var allWidgetIds = [
        'stats-row',
        'task-distribution',
        'upcoming-deadlines',
        'overdue-tasks',
        'project-status',
        'tasks-by-priority',
        'todays-tasks',
        'team-workload',
        'recent-open-tasks',
        'recent-projects'
    ];

    // Load saved widget state from localStorage
    function loadWidgetState() {
        var saved = localStorage.getItem('dashboard-widgets');
        if (saved) {
            try {
                return JSON.parse(saved);
            } catch (e) {
                return defaultWidgets.slice();
            }
        }
        return defaultWidgets.slice();
    }

    // Save widget state to localStorage
    function saveWidgetState(visibleWidgets) {
        localStorage.setItem('dashboard-widgets', JSON.stringify(visibleWidgets));
    }

    // Apply widget visibility to the DOM
    function applyWidgetVisibility(visibleWidgets) {
        allWidgetIds.forEach(function (widgetId) {
            var el = document.querySelector('[data-widget-id="' + widgetId + '"]');
            if (!el) return;

            if (visibleWidgets.indexOf(widgetId) !== -1) {
                if (el.style.display === 'none') {
                    el.style.display = '';
                    el.classList.add('widget-showing');
                    setTimeout(function () {
                        el.classList.remove('widget-showing');
                    }, 500);
                } else {
                    el.style.display = '';
                }
            } else {
                el.style.display = 'none';
            }
        });

        // Re-animate chart bars for newly shown widgets
        setTimeout(function () {
            document.querySelectorAll('.chart-bar-fill').forEach(function (bar) {
                var w = bar.getAttribute('data-width');
                if (w) bar.style.width = w + '%';
            });
        }, 100);
    }

    // Sync modal toggles with current state
    function syncModalToggles(visibleWidgets) {
        allWidgetIds.forEach(function (widgetId) {
            var toggle = document.querySelector('[data-widget-toggle="' + widgetId + '"]');
            if (toggle) {
                toggle.checked = visibleWidgets.indexOf(widgetId) !== -1;
            }
        });
    }

    // Initial load
    var currentVisibleWidgets = loadWidgetState();
    applyWidgetVisibility(currentVisibleWidgets);

    // ── Open modal ───────────────────────────────────────────────
    addWidgetBtn.addEventListener('click', function () {
        syncModalToggles(currentVisibleWidgets);
        widgetModal.classList.add('active');
        document.body.style.overflow = 'hidden';
    });

    // ── Close modal functions ────────────────────────────────────
    function closeWidgetModal() {
        widgetModal.classList.remove('active');
        document.body.style.overflow = '';
    }

    widgetModalClose.addEventListener('click', closeWidgetModal);
    widgetModalCancel.addEventListener('click', closeWidgetModal);

    // Close on overlay click
    widgetModal.addEventListener('click', function (e) {
        if (e.target === widgetModal) {
            closeWidgetModal();
        }
    });

    // Close on Escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && widgetModal.classList.contains('active')) {
            closeWidgetModal();
        }
    });

    // ── Toggle click on widget option row ────────────────────────
    document.querySelectorAll('.widget-option').forEach(function (option) {
        option.addEventListener('click', function (e) {
            // Don't toggle if they clicked the toggle itself
            if (e.target.closest('.toggle-switch')) return;
            var toggle = option.querySelector('input[type="checkbox"]');
            if (toggle) {
                toggle.checked = !toggle.checked;
            }
        });
    });

    // ── Apply changes ────────────────────────────────────────────
    widgetModalApply.addEventListener('click', function () {
        var newVisible = [];
        allWidgetIds.forEach(function (widgetId) {
            var toggle = document.querySelector('[data-widget-toggle="' + widgetId + '"]');
            if (toggle && toggle.checked) {
                newVisible.push(widgetId);
            }
        });

        currentVisibleWidgets = newVisible;
        saveWidgetState(currentVisibleWidgets);
        applyWidgetVisibility(currentVisibleWidgets);
        closeWidgetModal();
    });

});

// ── Confirm Dialog ───────────────────────────────────────────────
function showConfirmDialog(title, message, onConfirm) {
    // Create overlay
    var overlay = document.createElement('div');
    overlay.className = 'confirm-overlay';
    overlay.innerHTML =
        '<div class="confirm-dialog">' +
        '  <h3>' + title + '</h3>' +
        '  <p>' + message + '</p>' +
        '  <div class="confirm-actions">' +
        '    <button class="btn btn-secondary cancel-btn">Cancel</button>' +
        '    <button class="btn btn-danger confirm-btn">Delete</button>' +
        '  </div>' +
        '</div>';

    document.body.appendChild(overlay);

    // Show with animation
    requestAnimationFrame(function () {
        overlay.classList.add('active');
    });

    // Cancel
    overlay.querySelector('.cancel-btn').addEventListener('click', function () {
        closeDialog(overlay);
    });

    // Confirm
    overlay.querySelector('.confirm-btn').addEventListener('click', function () {
        closeDialog(overlay);
        onConfirm();
    });

    // Click outside
    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) {
            closeDialog(overlay);
        }
    });

    // Escape key
    function handleEsc(e) {
        if (e.key === 'Escape') {
            closeDialog(overlay);
            document.removeEventListener('keydown', handleEsc);
        }
    }
    document.addEventListener('keydown', handleEsc);
}

function closeDialog(overlay) {
    overlay.classList.remove('active');
    setTimeout(function () {
        overlay.remove();
    }, 200);
}
