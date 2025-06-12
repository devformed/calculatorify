export interface HistoryItem {
  title: string;
  link: string;
}

export interface HistorySection {
  group: string;
  items: HistoryItem[];
}

const historyData: HistorySection[] = [
  {
    group: "Today",
    items: [
      { title: "Salary Growth Calculator", link: "#" }
    ]
  },
  {
    group: "Yesterday",
    items: [
      { title: "Loan Repayment Calculator", link: "#" }
    ]
  },
  {
    group: "Last week",
    items: [
      { title: "Tax Estimator", link: "#" },
      { title: "Savings Planner", link: "#" }
    ]
  }
];

export function renderHistory(): void {
  const container = document.getElementById("historyContainer");
  if (!container) return;
  historyData.forEach(section => {
    const secEl = document.createElement("div");
    secEl.className = "history-section";
    const header = document.createElement("div");
    header.className = "history-group";
    header.textContent = section.group;
    const list = document.createElement("ul");
    list.className = "history-list";
    section.items.forEach(item => {
      const li = document.createElement("li");
      const a = document.createElement("a");
      a.href = item.link;
      a.textContent = item.title;
      li.appendChild(a);
      list.appendChild(li);
    });
    secEl.append(header, list);
    container.appendChild(secEl);
  });
}