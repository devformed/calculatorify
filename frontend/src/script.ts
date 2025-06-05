document.addEventListener("DOMContentLoaded", () => {
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
  const navToggle = document.getElementById("navToggle");
  const navMenu = document.getElementById("navMenu");
  if (navToggle && navMenu) {
    navToggle.addEventListener("click", () => {
      navMenu.classList.toggle("hidden");
    });
  }
  const signInButton = document.getElementById("signIn");
  if (signInButton) {
    signInButton.addEventListener("click", () => {
      window.location.href = "dashboard.html";
    });
  }
  const goBack = document.getElementById("goBack");
  if (goBack) {
    goBack.addEventListener("click", () => {
      window.location.href = "dashboard.html";
    });
  }
  const updatePasswordButton = document.getElementById("updatePassword");
  if (updatePasswordButton) {
    updatePasswordButton.addEventListener("click", () => {
      alert("Password update functionality coming soon.");
    });
  }
});