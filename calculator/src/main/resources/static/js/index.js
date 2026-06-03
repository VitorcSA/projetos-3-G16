import {openProfileDialog} from './profile.js'

document.getElementById("profile_button").addEventListener("click", () => {
    openProfileDialog(document.getElementById("profile_dialog"));
});

fetch('/api/calculator/calculate', {
	method: 'POST',
	credentials: 'include'
}).then(async response => {
	if (!response.ok) return;

	const data = await response.json();
	const div = document.getElementById('calculation_info');

	showCalculation(data);
});

function showCalculation(data) {
    const trees = Math.floor(data.difference / 22);

    document.getElementById('saved_value').innerText = `${data.difference.toFixed(3)} kg`;
    document.getElementById('physical_value').innerText = `${data.physical_card_emission.toFixed(3)} Kg ↑ ${data.reduction_percentage.toFixed(0)}%`;
    document.getElementById('physical_cards').innerText = `Equivalente a ${data.staff_count} cartões`;
    document.getElementById('digital_per_card_value').innerText = `${data.digital_card_emission_per_card.toFixed(3)} Kg ↑ ${data.reduction_percentage.toFixed(0)}%`;
    document.getElementById('trees_value').innerText = trees;
}