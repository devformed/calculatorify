import { initNavToggle } from "./nav.js";

document.addEventListener("DOMContentLoaded", () => {
  initNavToggle();
  const signInButton = document.getElementById("signIn");
  if (signInButton) {
    signInButton.addEventListener("click", () => {
      window.location.href = "dashboard.html";
    });
  }
});