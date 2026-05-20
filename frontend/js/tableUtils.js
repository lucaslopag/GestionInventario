/**
 * utilidades compartidas para hacer las tablas redimensionables
 * y poder ocultar/mostrar columnas dinámicamente.
 */

function initTableUtils(tableId, toggleContainerId) {
    const table = document.getElementById(tableId);
    const toggleContainer = document.getElementById(toggleContainerId);
    
    if (!table) return;

    // 1. Redimensionamiento
    makeTableResizable(table);

    // 2. Visibilidad (Mostrar/Ocultar)
    if (toggleContainer) {
        createColumnToggle(table, toggleContainer);
    }
}

function makeTableResizable(table) {
    const cols = table.querySelectorAll('th');
    
    // Necesitamos asegurarnos de que la tabla tenga layout fixed para que resizer funcione bien
    table.style.tableLayout = 'fixed';
    table.style.width = '100%';

    [].forEach.call(cols, function(th) {
        // Añadir div resizer
        const resizer = document.createElement('div');
        resizer.classList.add('col-resizer');
        th.appendChild(resizer);
        th.style.position = 'relative';

        createResizableColumn(th, resizer);
    });
}

function createResizableColumn(th, resizer) {
    let x = 0;
    let w = 0;

    const mouseDownHandler = function(e) {
        x = e.clientX;
        const styles = window.getComputedStyle(th);
        w = parseInt(styles.width, 10);

        document.addEventListener('mousemove', mouseMoveHandler);
        document.addEventListener('mouseup', mouseUpHandler);
        resizer.classList.add('resizing');
    };

    const mouseMoveHandler = function(e) {
        const dx = e.clientX - x;
        th.style.width = `${w + dx}px`;
    };

    const mouseUpHandler = function() {
        document.removeEventListener('mousemove', mouseMoveHandler);
        document.removeEventListener('mouseup', mouseUpHandler);
        resizer.classList.remove('resizing');
    };

    resizer.addEventListener('mousedown', mouseDownHandler);
}

function createColumnToggle(table, container) {
    const thead = table.querySelector('thead');
    if (!thead) return;

    const headers = thead.querySelectorAll('th');
    if (headers.length === 0) return;

    // Crear el dropdown
    const dropdownWrapper = document.createElement('div');
    dropdownWrapper.classList.add('column-toggle-wrapper');
    
    const btn = document.createElement('button');
    btn.classList.add('btn-secondary');
    btn.innerHTML = '<i data-lucide="columns"></i> Columnas';
    btn.title = 'Mostrar/Ocultar Columnas';
    
    const menu = document.createElement('div');
    menu.classList.add('column-toggle-menu');

    // Añadir checkbox por cada columna
    headers.forEach((th, index) => {
        // Ignorar columnas vacías (ej: acciones a veces)
        const text = th.innerText.trim();
        const labelText = text || 'Columna ' + (index + 1);

        const label = document.createElement('label');
        label.classList.add('column-toggle-item');
        
        const cb = document.createElement('input');
        cb.type = 'checkbox';
        cb.checked = true; // por defecto visibles
        cb.addEventListener('change', (e) => {
            toggleColumnVisibility(table, index, e.target.checked);
        });

        label.appendChild(cb);
        label.appendChild(document.createTextNode(' ' + labelText));
        menu.appendChild(label);
    });

    dropdownWrapper.appendChild(btn);
    dropdownWrapper.appendChild(menu);
    container.appendChild(dropdownWrapper);

    // Eventos para abrir/cerrar menú
    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        menu.classList.toggle('show');
    });

    document.addEventListener('click', (e) => {
        if (!dropdownWrapper.contains(e.target)) {
            menu.classList.remove('show');
        }
    });
}

function toggleColumnVisibility(table, colIndex, isVisible) {
    const rows = table.querySelectorAll('tr');
    const displayStyle = isVisible ? '' : 'none';
    
    rows.forEach(row => {
        const cell = row.children[colIndex];
        if (cell) {
            cell.style.display = displayStyle;
        }
    });
}

// Interceptar renderizaciones dinámicas en tablas que se sobreescriben enteras (innerHTML en tbody)
// Usamos MutationObserver para re-aplicar estilos ocultos si el checkbox dice que está oculta.
function observeTableChanges(tableId, toggleContainerId) {
    const table = document.getElementById(tableId);
    const container = document.getElementById(toggleContainerId);
    if (!table || !container) return;

    const tbody = table.querySelector('tbody');
    if (!tbody) return;

    const observer = new MutationObserver(() => {
        // Buscar el menu
        const menu = container.querySelector('.column-toggle-menu');
        if (!menu) return;
        const checkboxes = menu.querySelectorAll('input[type="checkbox"]');
        
        checkboxes.forEach((cb, index) => {
            if (!cb.checked) {
                // Forzar ocultamiento en el nuevo HTML
                toggleColumnVisibility(table, index, false);
            }
        });
    });

    observer.observe(tbody, { childList: true, subtree: true });
}
