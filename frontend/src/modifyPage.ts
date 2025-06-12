import { initNavToggle } from "./nav";

document.addEventListener("DOMContentLoaded", () => {
  initNavToggle();
  const modInput1 = document.getElementById("modInput1") as HTMLInputElement | null;
  const modValue1 = document.getElementById("modValue1") as HTMLElement | null;
  if (modInput1 && modValue1) {
    modValue1.textContent = modInput1.value;
    modInput1.addEventListener("input", () => {
      modValue1.textContent = modInput1.value;
    });
  }
  const modInput2 = document.getElementById("modInput2") as HTMLInputElement | null;
  const modValue2 = document.getElementById("modValue2") as HTMLElement | null;
  if (modInput2 && modValue2) {
    modValue2.textContent = `${modInput2.value}%`;
    modInput2.addEventListener("input", () => {
      modValue2.textContent = `${modInput2.value}%`;
    });
  }
});