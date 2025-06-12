interface DashboardCard {
  title: string;
  description: string;
}

const cardsData: DashboardCard[] = [
  {
    title: "Salary Growth Calculator",
    description:
      "Calculates your projected salary after a specified period, assuming a fixed percentage raise at regular intervals.",
  },
  {
    title: "Loan Repayment Calculator",
    description:
      "Estimate your loan repayment schedule, including interest and principal over time.",
  },
  {
    title: "Tax Estimator",
    description:
      "Approximate your annual tax liability based on income, deductions, and filing status.",
  },
  {
    title: "Savings Planner",
    description:
      "Plan your savings goals by calculating required deposits to reach a target amount over time.",
  },
];

export function renderDashboardCards(): void {
  const container = document.getElementById("cardsContainer");
  if (!container) return;
  cardsData.forEach((card) => {
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
}