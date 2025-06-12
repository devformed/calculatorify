import { initNavToggle } from "./nav";

document.addEventListener("DOMContentLoaded", () => {
  initNavToggle();
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