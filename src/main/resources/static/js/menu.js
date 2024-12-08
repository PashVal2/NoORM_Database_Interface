document.addEventListener('click', function(event) {
    const menu = document.querySelector('.dropdown-menu');
    if (menu) {
        menu.style.display = 'none';
    }
});

document.querySelector('.dropdown-toggle')?.addEventListener('click', function(event) {
    const menu = document.querySelector('.dropdown-menu');

    if (menu) {
        event.stopPropagation();
        if (menu.style.display === 'flex') {
            menu.style.display = 'none';
        }
        else {
            menu.style.display = 'flex';
            menu.style.flexDirection = 'column';
        }
    }
});




