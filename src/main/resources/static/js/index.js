document.addEventListener('DOMContentLoaded', function () {
    const ctx = document.getElementById('connectionsChart').getContext('2d');
    const okCount = parseInt(document.getElementById('okCount').value, 10) || 0;
    const nokCount = parseInt(document.getElementById('nokCount').value, 10) || 0;

    const totalCount = okCount + nokCount;
    const okPercentage = totalCount > 0 ? (okCount / totalCount * 100).toFixed(2) : 0;
    const nokPercentage = totalCount > 0 ? (nokCount / totalCount * 100).toFixed(2) : 0;

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: [`Conexões OK: ${okPercentage}%`, `Conexões NOK: ${nokPercentage}%`],
            datasets: [{
                label: 'Distribuição de Conexões',
                data: [okCount, nokCount],
                backgroundColor: [
                    'rgba(40, 167, 69, 0.6)', 
                    'rgba(220, 53, 69, 0.6)'
                ],
                borderColor: [
                    'rgba(40, 167, 69, 1)',
                    'rgba(220, 53, 69, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'Distribuição de Conexões'
                }
            }
        }
    });
});