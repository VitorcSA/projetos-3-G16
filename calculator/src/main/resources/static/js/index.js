const donutCtx = document.getElementById('donutChart').getContext('2d');

new Chart(donutCtx, {
	type: 'doughnut',
	data: {
		labels: ['Transporte', 'Produção', 'Descarte'],
		datasets: [{
			data: [
				data.transportEmissionPercentage,
				data.productionEmissionPercentage,
				data.disposalEmissionPercentage
			],
			backgroundColor: [
				'#3D52A0',  // azul escuro
				'#7091E6',  // azul médio
				'#C5CAE9'   // azul claro
			],
			borderWidth: 0,
			hoverOffset: 8
		}]
	},
	options: {
		responsive: true,
		maintainAspectRatio: false,
		cutout: '70%',
		plugins: {
			legend: {
				display: false // ← desliga a legenda padrão
			},
			tooltip: {
				callbacks: {
					label: (ctx) => ` ${ctx.label}: ${ctx.parsed.toFixed(1)}%`
				}
			}
		}
	}
});

const mainCtx = document.getElementById('mainChart').getContext('2d');

const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
const mensal = (anual) => meses.map(() => +(anual / 12).toFixed(2));

const fisico = mensal(data.annualPhysicEmission);
const digital = mensal(data.annualDigitalEmission);
const diferenca = fisico.map((v, i) => +(v - digital[i]).toFixed(2));

new Chart(mainCtx, {
	type: 'line',
	data: {
		labels: meses,
		datasets: [
			{
				label: 'Cartão Físico',
				data: fisico,
				borderColor: '#D52B1E',
				backgroundColor: 'rgba(213, 43, 30, 0.08)',
				fill: true,
				tension: 0.4,
				pointRadius: 4
			},
			{
				label: 'Cartão Digital',
				data: digital,
				borderColor: '#277D5F',
				backgroundColor: 'rgba(39, 125, 95, 0.08)',
				fill: true,
				tension: 0.4,
				pointRadius: 4
			},
			{
				label: 'Diferença (CO₂ evitado)',
				data: diferenca,
				borderColor: '#F5A623',
				backgroundColor: 'rgba(245, 166, 35, 0.08)',
				fill: true,
				tension: 0.4,
				pointRadius: 4,
				borderDash: [6, 3] // ← linha tracejada para diferenciar
			}
		]
	},
	options: {
		responsive: true,
		interaction: {
			mode: 'index',
			intersect: false // ← mostra tooltip de todas as linhas ao hover
		},
		plugins: {
			legend: {
				position: 'bottom'
			},
			tooltip: {
				callbacks: {
					label: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y} kg`
				}
			}
		},
		scales: {
			y: {
				beginAtZero: true,
				ticks: {
					callback: (v) => `${v} kg`
				}
			}
		}
	}
});

const co2Saved = data.annualPhysicEmission - data.annualDigitalEmission;

const equivalences = {
	water: (co2Saved * 1.67).toFixed(0),
	trees: (co2Saved * 0.02).toFixed(0),
	gasoline: (co2Saved * 0.43).toFixed(0),
	bottles: (co2Saved * 25).toFixed(0)
};

document.getElementById('eq-water').textContent = (co2Saved * 1.67).toFixed(0);
document.getElementById('eq-trees').textContent = (co2Saved * 0.02).toFixed(0);
document.getElementById('eq-gasoline').textContent = (co2Saved * 0.43).toFixed(0);
document.getElementById('eq-bottles').textContent = (co2Saved * 25).toFixed(0);
