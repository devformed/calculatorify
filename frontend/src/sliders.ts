/**
 * Initializes range inputs (sliders) and their display values.
 */
export function initSliders(): void {
  const salarySlider = document.getElementById("salarySlider") as HTMLInputElement | null;
  const salaryValue = document.getElementById("salaryValue") as HTMLElement | null;
  if (salarySlider && salaryValue) {
    salaryValue.textContent = `$${salarySlider.value}`;
    salarySlider.addEventListener("input", () => {
      salaryValue.textContent = `$${salarySlider.value}`;
    });
  }
  const raiseSlider = document.getElementById("raiseSlider") as HTMLInputElement | null;
  const raiseValue = document.getElementById("raiseValue") as HTMLElement | null;
  if (raiseSlider && raiseValue) {
    raiseValue.textContent = `${raiseSlider.value}%`;
    raiseSlider.addEventListener("input", () => {
      raiseValue.textContent = `${raiseSlider.value}%`;
    });
  }
}