// Função chamada sempre que o tipo (URL/Telnet) é alterado
function toggleFields() {
    const type = document.getElementById('type').value;

    // Pega os elementos do formulário pelo ID
    const urlField = document.getElementById('urlField');
    const urlInput = document.getElementById('address');

    const telnetFields = document.getElementById('telnetFields');
    const hostnameInput = document.getElementById('hostname');
    const portInput = document.getElementById('port');

    // Se o tipo for URL...
    if (type === 'url') {
        urlField.classList.remove('d-none'); // Mostra a div da URL
        urlInput.disabled = false;           // ATIVA o input da URL

        telnetFields.classList.add('d-none'); // Esconde a div do Telnet
        hostnameInput.disabled = true;        // DESATIVA o input do Hostname
        portInput.disabled = true;            // DESATIVA o input da Porta
    } else { // Se o tipo for Telnet...
        urlField.classList.add('d-none');   // Esconde a div da URL
        urlInput.disabled = true;           // DESATIVA o input da URL

        telnetFields.classList.remove('d-none'); // Mostra a div do Telnet
        hostnameInput.disabled = false;          // ATIVA o input do Hostname
        portInput.disabled = false;            // ATIVA o input da Porta
    }
}

// Executa a função assim que a página carrega para configurar o estado inicial correto
document.addEventListener('DOMContentLoaded', toggleFields);