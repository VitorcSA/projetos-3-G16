export function openDialog(dialog){
	document.querySelectorAll("dialog[open]").forEach(d => d.close());
	dialog.showModal();
}