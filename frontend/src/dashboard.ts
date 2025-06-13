interface DashboardCard {
  title: string;
  description: string;
}

export async function renderDashboardCards(): Promise<void> {
  const container = document.getElementById("cardsContainer");
  if (!container) return;
  try {
    const response = await fetch("http://localhost:8080/calculators", { method: 'GET', credentials: "include" });
    if (!response.ok) {
      console.error("Failed to load calculators:", response.status);
      return;
    }
    const cards: DashboardCard[] = await response.json();
    cards.forEach((card) => {
      const cardEl = document.createElement("section");
      cardEl.className = "dashboard-card";
      const heading = document.createElement("h2");
      heading.textContent = card.title;
      const desc = document.createElement("p");
      desc.textContent = card.description;
      const btn = document.createElement("button");
      btn.className = "btn btn-primary";
      btn.textContent = "Calculate";
      btn.addEventListener("click", () => {
        alert(`Clicked ${card.title}`);
      });
      cardEl.append(heading, desc, btn);
      container.append(cardEl);
    });
  } catch (error) {
    console.error("Error fetching calculator cards:", error);
  }
}