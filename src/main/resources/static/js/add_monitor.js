function toggleFields() {
    const type = document.getElementById('type').value;

    const urlField = document.getElementById('url-field');
    const urlInput = document.getElementById('address');

    const telnetFields = document.getElementById('telnet-fields');
    const hostnameInput = document.getElementById('hostname');
    const portInput = document.getElementById('port');

    if (type === 'url') {
        urlField.classList.remove('d-none');
        urlInput.disabled = false; // Ativa o campo de URL

        telnetFields.classList.add('d-none');
        hostnameInput.disabled = true; // Desativa os campos de Telnet
        portInput.disabled = true;
    } else { // type === 'telnet'
        urlField.classList.add('d-none');
        urlInput.disabled = true; // Desativa o campo de URL

        telnetFields.classList.remove('d-none');
        hostnameInput.disabled = false; // Ativa os campos de Telnet
        portInput.disabled = false;
    }
}

// Garante que os campos corretos são exibidos e ativados ao carregar a página
document.addEventListener('DOMContentLoaded', toggleFields);