// Centered confirm dialog helper.
function appConfirm(message) {
    return new Promise((resolve) => {
        const overlay = document.createElement('div');
        overlay.className = 'app-confirm-overlay';
        overlay.innerHTML = `
            <div class="app-confirm-dialog" role="dialog" aria-modal="true">
                <div class="app-confirm-title">操作确认</div>
                <div class="app-confirm-message"></div>
                <div class="app-confirm-actions">
                    <button type="button" class="btn btn-secondary" data-action="cancel">取消</button>
                    <button type="button" class="btn btn-primary" data-action="ok">确定</button>
                </div>
            </div>
        `;

        const messageNode = overlay.querySelector('.app-confirm-message');
        messageNode.textContent = message || '确认继续操作吗？';

        const close = (result) => {
            document.removeEventListener('keydown', onKeyDown);
            overlay.remove();
            resolve(result);
        };

        const onKeyDown = (event) => {
            if (event.key === 'Escape') {
                close(false);
            }
        };

        overlay.addEventListener('click', (event) => {
            if (event.target === overlay) {
                close(false);
            }
        });

        overlay.querySelector('[data-action="cancel"]').addEventListener('click', () => close(false));
        overlay.querySelector('[data-action="ok"]').addEventListener('click', () => close(true));

        document.addEventListener('keydown', onKeyDown);
        document.body.appendChild(overlay);
    });
}

function appAlert(message, title) {
    return new Promise((resolve) => {
        const overlay = document.createElement('div');
        overlay.className = 'app-confirm-overlay';
        overlay.innerHTML = `
            <div class="app-confirm-dialog" role="dialog" aria-modal="true">
                <div class="app-confirm-title"></div>
                <div class="app-confirm-message"></div>
                <div class="app-confirm-actions">
                    <button type="button" class="btn btn-primary" data-action="ok">确定</button>
                </div>
            </div>
        `;

        overlay.querySelector('.app-confirm-title').textContent = title || '提示';
        overlay.querySelector('.app-confirm-message').textContent = message || '';

        const close = () => {
            document.removeEventListener('keydown', onKeyDown);
            overlay.remove();
            resolve(true);
        };

        const onKeyDown = (event) => {
            if (event.key === 'Escape' || event.key === 'Enter') {
                close();
            }
        };

        overlay.addEventListener('click', (event) => {
            if (event.target === overlay) {
                close();
            }
        });

        overlay.querySelector('[data-action="ok"]').addEventListener('click', close);

        document.addEventListener('keydown', onKeyDown);
        document.body.appendChild(overlay);
    });
}
