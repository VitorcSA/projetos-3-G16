const goalTypeLabels = {
	CO2_EVITADO_ACUMULADO: 'CO2 Evitado Acumulado',
	INDICE_MIGRACAO_DIGITAL: 'Índice de Migração Digital',
	DINHEIRO_ECONOMIZADO: 'Dinheiro Economizado'
};

const goalUnits = {
	CO2_EVITADO_ACUMULADO: 'Kg',
	INDICE_MIGRACAO_DIGITAL: '%',
	DINHEIRO_ECONOMIZADO: 'R$'
};

function formatNumber(value) {
	return new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}

function formatDate(dateString) {
	const date = new Date(dateString + 'T00:00:00');
	return date.toLocaleDateString('pt-BR', { month: 'short', year: 'numeric' });
}

async function loadSummary() {
	try {
		const response = await fetch('/api/summary', { credentials: 'include' });
		if (!response.ok) throw new Error(`Erro ao carregar sumário: ${response.status}`);
		const data = await response.json();

		document.getElementById('hist_company_name').innerText = data.company_name || '-';
		document.getElementById('hist_brand').innerText = data.company_name || '-';
		document.getElementById('hist_sector').innerText = data.industry_sector || 'Não informado';
		document.getElementById('hist_location').innerText = (data.city && data.state)
			? `${data.city} - ${data.state}` : 'Não informado';

		document.getElementById('hist_quality_bar').style.width = `${Math.min(data.quality_score, 100)}%`;

		document.getElementById('hist_physical_emission').innerText = `${formatNumber(data.physical_card_emission)} kg`;
		document.getElementById('hist_digital_emission').innerText = `${formatNumber(data.digital_card_emission)} kg`;
		document.getElementById('hist_money_wasted').innerText = `R$ ${formatNumber(data.money_wasted)}`;

		document.getElementById('hist_summary').innerText = data.summary;
	} catch {
		document.getElementById('hist_summary').innerText = 'Erro ao carregar dados.';
	}
}

const goalsList = document.getElementById('goals-list');
const goalModal = document.getElementById('goal-modal');
const goalForm = document.getElementById('goal-form');
const goalError = document.getElementById('goal-error');
const btnAddGoal = document.getElementById('btn-add-goal');
const btnDeleteGoal = document.getElementById('btn-delete-goal');

async function loadGoals() {
	try {
		const response = await fetch('/api/goals', { credentials: 'include' });
		if (!response.ok) throw new Error('Erro ao carregar metas');
		const goals = await response.json();

		goalsList.innerHTML = '';

		if (goals.length === 0) {
			goalsList.innerHTML = '<p class="card-description">Nenhuma meta cadastrada ainda.</p>';
			return;
		}

		goals.forEach(goal => {
			const progress = goal.target_value > 0
				? Math.min((goal.current_value / goal.target_value) * 100, 100)
				: 0;

			const card = document.createElement('div');
			card.className = 'goal-card';
			card.innerHTML = `
				<p class="card-label">${goal.type_label}</p>
				<h3>${formatNumber(goal.current_value)}${goal.unit}</h3>
				<div class="goal-progress-bar">
					<div class="goal-progress-fill" style="width:${progress}%"></div>
				</div>
				<p class="card-description">Meta: ${formatNumber(goal.target_value)}${goal.unit} · Até ${formatDate(goal.target_date)}</p>
			`;
			card.addEventListener('click', () => openGoalModal(goal));
			goalsList.appendChild(card);
		});
	} catch {
		goalsList.innerHTML = '<p class="error-message">Erro ao carregar metas.</p>';
	}
}

function openGoalModal(goal) {
	goalError.innerText = '';
	goalForm.reset();

	if (goal) {
		goalForm.goal_id.value = goal.id;
		goalForm.type.value = goal.type;
		goalForm.target_value.value = goal.target_value;
		goalForm.target_date.value = goal.target_date;
		btnDeleteGoal.style.display = 'inline-block';
	} else {
		goalForm.goal_id.value = '';
		btnDeleteGoal.style.display = 'none';
	}

	goalModal.showModal();
}

btnAddGoal.addEventListener('click', () => openGoalModal(null));

goalForm.addEventListener('submit', event => {
	event.preventDefault();
	goalError.innerText = '';

	const goalId = goalForm.goal_id.value;
	const payload = {
		type: goalForm.type.value,
		target_value: parseFloat(goalForm.target_value.value),
		target_date: goalForm.target_date.value
	};

	const url = goalId ? `/api/goals/${goalId}` : '/api/goals';
	const method = goalId ? 'PUT' : 'POST';

	fetch(url, {
		method,
		headers: { 'Content-Type': 'application/json' },
		credentials: 'include',
		body: JSON.stringify(payload)
	}).then(async response => {
		if (response.ok) {
			goalModal.close();
			loadGoals();
		} else {
			const text = await response.text();
			goalError.innerText = text;
		}
	}).catch(() => {
		goalError.innerText = 'Erro ao salvar meta';
	});
});

btnDeleteGoal.addEventListener('click', () => {
	const goalId = goalForm.goal_id.value;
	if (!goalId) return;

	fetch(`/api/goals/${goalId}`, { method: 'DELETE', credentials: 'include' })
		.then(response => {
			if (response.ok) {
				goalModal.close();
				loadGoals();
			} else {
				goalError.innerText = 'Erro ao excluir meta';
			}
		}).catch(() => {
			goalError.innerText = 'Erro ao excluir meta';
		});
});

loadSummary();
loadGoals();