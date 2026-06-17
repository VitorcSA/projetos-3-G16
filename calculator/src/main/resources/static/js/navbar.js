import { loadStates, loadCities } from './address.js';

const navbar = document.getElementById('navigation_bar');
const links = navbar.querySelectorAll('[data-path]');

links.forEach(link => {

	if (link.dataset.path === window.location.pathname) {
		link.classList.add('active');
	}

});

const buttons = navbar.querySelectorAll('[data-modal]');

buttons.forEach(button => {
	button.addEventListener('click', () => {
		const modal = document.getElementById(button.dataset.modal);
		modal.showModal();
	});
});

const form = document.getElementById('profile-form');
const state = document.getElementById('profile-state');
const city = document.getElementById('profile-city');

const currentState = state.dataset.selected;
const currentCity = city.dataset.selected;

await loadStates(state);

if (currentState) {
	state.value = currentState;
	await loadCities(state, city);
	city.value = currentCity;
	city.required = true;
}

state.addEventListener('change', async () => {
	await loadCities(state, city);
	city.required = state.value !== '';
});

form.addEventListener('submit', event => {
	event.preventDefault();
	const payload = {
		name: form.name.value,
		email: form.email.value,
		password: form.password.value,
		staff_count: form.staff_count.value,
		address: state.value ? {
			city: city.value,
			state: state.value,
		} : null,
		digital_card_staff_count: form.digital_staff_count.value
			? parseInt(form.digital_staff_count.value)
			: null
	};

	fetch('/api/user/profile', {
		method: 'PUT',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	}).then(async response => {
		const text = await response.text();
		if (response.ok) {
			location.reload();
		} else {
			document.getElementById('profile-error').innerText = text;
		}
	}).catch(() => {
		alert('Erro');
	});
});

const exportModal = document.getElementById('export-modal');
const dashboardSelect = document.getElementById('dashboard-selection');
const btnExportExcel = document.getElementById('btn-export-excel');
const btnExportPdf = document.getElementById('btn-export-pdf');
const btnExportImage = document.getElementById('btn-export-image');
const exportError = document.getElementById('export-error');

let dashboards = [];

async function loadDashboards(select) {
	try {
		const response = await fetch('/api/dashboards');
		if (!response.ok) throw new Error(`HTTP ${response.status}`);
		dashboards = await response.json();

		select.innerHTML = '<option value="">Selecionar Dashboard</option>';

		dashboards.forEach(dashboard => {
			const option = document.createElement('option');
			option.value = dashboard.id;
			option.innerText = dashboard.name;
			select.appendChild(option);
		});
	} catch {
		exportError.innerText = 'Erro ao carregar dashboards';
	}
}

await loadDashboards(dashboardSelect);

exportModal.addEventListener('close', () => {
	exportError.innerText = '';
	dashboardSelect.value = '';
});

function exportDashboardFile(dashboardId, format, filename) {
	fetch(`/api/dashboards/${dashboardId}/export?format=${format}`)
		.then(async response => {
			if (!response.ok) {
				const text = await response.text();
				throw new Error(text);
			}
			return response.blob();
		})
		.then(blob => {
			const url = URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = url;
			link.download = filename;
			link.click();
			URL.revokeObjectURL(url);
			exportModal.close();
		})
		.catch(() => {
			exportError.innerText = 'Erro ao exportar';
		});
}

async function exportDashboardImage(dashboard) {
	if (window.location.pathname !== dashboard.path) {
		exportError.innerText = `Acesse a tela "${dashboard.name}" para exportar como imagem`;
		return;
	}

	if (typeof html2canvas !== 'function') {
		exportError.innerText = 'Exportação de imagem não disponível nesta página';
		return;
	}

	const target = document.querySelector('.main-content');
	if (!target) {
		exportError.innerText = 'Erro ao exportar';
		return;
	}

	try {
		const canvas = await html2canvas(target);
		const link = document.createElement('a');
		link.href = canvas.toDataURL('image/png');
		link.download = 'dashboard.png';
		link.click();
		exportModal.close();
	} catch {
		exportError.innerText = 'Erro ao exportar';
	}
}

function handleExport(format) {
	const dashboardId = dashboardSelect.value;

	if (!dashboardId) {
		exportError.innerText = 'Selecione um dashboard antes de exportar';
		return;
	}

	exportError.innerText = '';

	if (format === 'excel') {
		exportDashboardFile(dashboardId, 'excel', 'dashboard.xlsx');
		return;
	}

	if (format === 'pdf') {
		exportDashboardFile(dashboardId, 'pdf', 'dashboard.pdf');
		return;
	}

	const dashboard = dashboards.find(d => d.id === dashboardId);
	exportDashboardImage(dashboard);
}

btnExportExcel.addEventListener('click', () => handleExport('excel'));
btnExportPdf.addEventListener('click', () => handleExport('pdf'));
btnExportImage.addEventListener('click', () => handleExport('image'));