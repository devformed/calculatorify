import {initNavToggle} from "./nav.js";

document.addEventListener("DOMContentLoaded", () => {
    initNavToggle();
    const signInButton = document.getElementById("signIn");
    if (signInButton) {
        signInButton.addEventListener("click", async () => {
            const emailInput = document.getElementById("email") as HTMLInputElement;
            const passwordInput = document.getElementById("password") as HTMLInputElement;
            const username = emailInput?.value.trim() ?? "";
            const password = passwordInput?.value ?? "";
            if (!username || !password) {
                alert("Please enter both email and password.");
                return;
            }
            try {
                const response = await fetch("http://localhost:8080/login", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    credentials: "include",
                    body: JSON.stringify({username, password})
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    alert(`Login failed: ${errorText}`);
                    return;
                }
                window.location.href = "dashboard.html";
            } catch (err) {
                console.error("Login error:", err);
                alert("An error occurred while logging in. Please try again.");
            }
        });
    }
});