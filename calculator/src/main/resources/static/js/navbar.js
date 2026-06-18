import { loadStates, loadCities } from './address.js';


const navbar = document.getElementById('navigation_bar');
if (navbar) {
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
			if (modal) modal.showModal();
		});
	});
}


const form = document.getElementById('profile-form');
const state = document.getElementById('profile-state');
const city = document.getElementById('profile-city');

if (form) {
	form.addEventListener('submit', event => {
		event.preventDefault();
		const payload = {
			name: form.name.value,
			email: form.email.value,
			password: form.password.value,
			staff_count: form.staff_count.value,
			address: state && state.value ? {
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
				const errorEl = document.getElementById('profile-error');
				if (errorEl) errorEl.innerText = text;
			}
		}).catch(() => {
			alert('Erro');
		});
	});
}


const exportModal = document.getElementById('export-modal');
const dashboardSelect = document.getElementById('dashboard-selection');
const btnExportExcel = document.getElementById('btn-export-excel');
const btnExportPdf = document.getElementById('btn-export-pdf');
const btnExportImage = document.getElementById('btn-export-image');
const exportError = document.getElementById('export-error');

let dashboards = [];

async function loadDashboards(select) {
	try {
		console.log("Tentando buscar dashboards em: /api/dashboards");
		const response = await fetch('/api/dashboards');

		console.log("Status da resposta do servidor:", response.status);
		if (!response.ok) throw new Error(`HTTP ${response.status}`);

		dashboards = await response.json();
		console.log("Dashboards recebidos com sucesso:", dashboards);

		select.innerHTML = '<option value="">Selecionar Dashboard</option>';

		dashboards.forEach(dashboard => {
			const option = document.createElement('option');
			option.value = dashboard.id;
			option.innerText = dashboard.name;
			select.appendChild(option);
		});

		console.log("Select preenchido no HTML!");
	} catch (error) {
		console.error("Erro detalhado capturado no Fetch:", error);
		if (exportError) exportError.innerText = 'Erro ao carregar dashboards';
	}
}

if (exportModal) {
	exportModal.addEventListener('close', () => {
		if (exportError) exportError.innerText = '';
		if (dashboardSelect) dashboardSelect.value = '';
	});
}

function exportDashboardFile(dashboardId, format, filename) {
	fetch(`/api/dashboards/${dashboardId}/export?format=${format}`)
		.then(async response => {
			if (!response.ok) {
				const text = await response.text();
				throw new Error(text || 'Erro interno do servidor');
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
			if (exportModal) exportModal.close();
		})
		.catch(error => {
			if (exportError) exportError.innerText = error.message || 'Erro ao exportar';
		});
}

async function exportDashboardImage(dashboard) {
	if (!dashboard) return;

	if (window.location.pathname !== dashboard.path) {
		if (exportError) exportError.innerText = `Acesse a tela "${dashboard.name}" para exportar como imagem`;
		return;
	}

	if (typeof html2canvas !== 'function') {
		if (exportError) exportError.innerText = 'Exportação de imagem não disponível nesta página';
		return;
	}

	const target = document.querySelector('.main-content');
	if (!target) {
		if (exportError) exportError.innerText = 'Erro ao exportar';
		return;
	}

	try {
		const canvas = await html2canvas(target);
		const link = document.createElement('a');
		link.href = canvas.toDataURL('image/png');
		link.download = `${dashboard.id}.png`;
		link.click();
		if (exportModal) exportModal.close();
	} catch {
		if (exportError) exportError.innerText = 'Erro ao exportar';
	}
}

function handleExport(format) {
	if (!dashboardSelect) return;
	const dashboardId = dashboardSelect.value;

	if (!dashboardId) {
		if (exportError) exportError.innerText = 'Selecione um dashboard antes de exportar';
		return;
	}

	if (exportError) exportError.innerText = '';

	if (format === 'excel') {
		exportDashboardFile(dashboardId, 'excel', `${dashboardId}.xlsx`);
		return;
	}

	if (format === 'pdf') {
		exportDashboardFile(dashboardId, 'pdf', `${dashboardId}.pdf`);
		return;
	}

	const dashboard = dashboards.find(d => d.id === dashboardId);
	exportDashboardImage(dashboard);
}

if (btnExportExcel) btnExportExcel.addEventListener('click', () => handleExport('excel'));
if (btnExportPdf) btnExportPdf.addEventListener('click', () => handleExport('pdf'));
if (btnExportImage) btnExportImage.addEventListener('click', () => handleExport('image'));



async function inicializarAplicacao() {
	console.log("Iniciando scripts da aplicação...");

	// Carrega os dashboards no modal
	if (dashboardSelect) {
		await loadDashboards(dashboardSelect);
	}

	if (state && city) {
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
	}
}

document.addEventListener('DOMContentLoaded', inicializarAplicacao);
