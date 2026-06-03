export async function loadStates(select) {
    const response = await fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome");
    const states = await response.json();
	
    states.forEach(state => {
        const option = document.createElement("option");
        option.value = state.nome;
        option.textContent = state.sigla;
        select.appendChild(option);
    });
}	

export async function loadCities(state, select){
    select.innerHTML = '<option value="">Selecione a cidade</option>';
    select.disabled = true;

    if (!state.value) return;

    const sigla = state.selectedOptions[0].textContent;

    const response = await fetch(`https://servicodados.ibge.gov.br/api/v1/localidades/estados/${sigla}/municipios`);
    const cities = await response.json();

    cities.forEach(city => {
        const option = document.createElement("option");
        option.value = city.nome;
        option.textContent = city.nome;
        select.appendChild(option);
    });

    select.disabled = false;
}