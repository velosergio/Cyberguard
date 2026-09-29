(function () {
    const select = document.querySelector("[data-rellenar-ip]");
    const ip = document.querySelector("[data-ip]");
    if (select && ip) {
        select.addEventListener("change", function () {
            const option = select.selectedOptions[0];
            if (option && option.dataset.ip) {
                ip.value = option.dataset.ip;
            }
        });
    }

    const fecha = document.querySelector("[data-ahora]");
    if (fecha && !fecha.value) {
        const ahora = new Date();
        ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
        fecha.value = ahora.toISOString().slice(0, 16);
    }
})();
