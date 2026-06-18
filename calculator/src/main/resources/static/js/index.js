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

const fisico = data.annualPhysicEmission;
const digital = data.annualDigitalEmission;
const economia = +(fisico - digital).toFixed(2);
const percentual = +((economia / fisico) * 100).toFixed(1);

new Chart(mainCtx, {
	type: 'bar',
	data: {
		labels: ['Emissão com\ncartão físico', 'Emissão com\ncartão digital', 'Economia de\nemissões'],
		datasets: [
			{
				label: 'Emissões',
				data: [fisico, digital, economia],
				backgroundColor: ['rgba(180,180,180,0.5)', 'rgba(180,180,180,0.5)', 'rgba(100,120,220,0.5)'],
				borderColor: ['rgba(180,180,180,0.8)', 'rgba(180,180,180,0.8)', 'rgba(100,120,220,0.9)'],
				borderWidth: 1,
				borderRadius: 4
			}
		]
	},
	options: {
		responsive: true,
		plugins: {
			legend: { display: false },
			tooltip: {
				callbacks: {
					label: (ctx) => ` ${ctx.parsed.y.toLocaleString('pt-BR')} kg CO₂`
				}
			},
			annotation: {
				annotations: {
					refLine: {
						type: 'line',
						yMin: fisico,
						yMax: fisico,
						borderColor: 'rgba(100,120,220,0.4)',
						borderWidth: 1.5,
						borderDash: [6, 4]
					},
					label: {
						type: 'label',
						xValue: 2,
						yValue: economia,
						content: [`${percentual}%`],
						backgroundColor: 'rgba(100,120,220,0.85)',
						color: '#fff',
						font: { size: 12, weight: 'bold' },
						padding: { x: 8, y: 4 },
						borderRadius: 4,
						yAdjust: -16
					}
				}
			}
		},
		scales: {
			x: {
				grid: { display: false }
			},
			y: {
				beginAtZero: true,
				ticks: {
					callback: (v) => `${v.toLocaleString('pt-BR')} kg`
				},
				grid: {
					color: 'rgba(0,0,0,0.05)'
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
