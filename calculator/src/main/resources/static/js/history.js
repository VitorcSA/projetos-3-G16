document.addEventListener("DOMContentLoaded", () => {
	const addRegisterBtn = document.querySelector(".welcome-section__add-register-btn");

	if (addRegisterBtn) {
		addRegisterBtn.addEventListener("click", async () => {
			try {
				const response = await fetch("/api/records/add", {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
				});

				if (response.ok) {
					const resultado = await response.json();
					alert(resultado.message);
					window.location.reload();
				} else {
					const erroTexto = await response.text();
					console.error("Erro no servidor:", erroTexto);
					alert("Erro ao adicionar registro: " + erroTexto);
				}

			} catch (error) {
				console.error("Erro na requisição Fetch:", error);
				alert("Não foi possível conectar ao servidor.");
			}
		});
	}
});

const records = window.monthly_records;

if (!records) {
	console.error("Variável monthly_records não encontrada no window!");
}

const labels = records.map(r => {
	const date = new Date(r.record_date);
	return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
});

const dataDigital = records.map(r => r.digital_staff_count);
const dataFisico = records.map(r => r.staff_count - r.digital_staff_count);

const ctx = document.getElementById('mainChart').getContext('2d');

new Chart(ctx, {
	type: 'bar',
	data: {
		labels: labels,
		datasets: [
			{
				label: 'Cartão Digital',
				data: dataDigital,
				backgroundColor: '#277D5F'
			},
			{
				label: 'Cartão Físico',
				data: dataFisico,
				backgroundColor: '#D52B1E'
			}
		]
	},
	options: {
		responsive: true,
		scales: {
			x: { stacked: true },
			y: { stacked: true, beginAtZero: true }
		}
	}
});

const percentage = window.current_goal_percentage || 0;
const restante = 100 - percentage;

const canvasRosca = document.getElementById('evolution_canvas');

if (canvasRosca) {
	new Chart(canvasRosca.getContext('2d'), {
		type: 'doughnut',
		data: {
			datasets: [{
				data: [percentage, restante],
				backgroundColor: ['#e63329', '#EAECEF'], borderWidth: 0,
				borderRadius: percentage > 0 ? [10, 0] : [0, 0]
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			cutout: '80%', plugins: {
				tooltip: { enabled: false }, legend: { display: false }
			}
		}
	});
} else {
	console.warn("Canvas 'evolution_canvas' não foi encontrado no HTML.");
}
