export interface HistoryItem {
  title: string;
  link: string;
}

interface HistoryRecord {
  userId: string;
  calculatorId: string;
  calculatorTitle: string;
  accessedAt: string;
}

export function renderHistory(): void {
  const container = document.getElementById("historyContainer");
  if (!container) return;
  container.innerHTML = "";
  fetch("http://localhost:8080/history", { method: "GET", credentials: "include" })
    .then(response => {
      if (!response.ok) {
        console.error("Failed to load history:", response.status);
        return [];
      }
      return response.json() as Promise<HistoryRecord[]>;
    })
    .then(data => {
      if (!Array.isArray(data)) return;
      const entries = data
        .slice()
        .sort(
          (a, b) =>
            new Date(b.accessedAt).getTime() -
            new Date(a.accessedAt).getTime()
        );
      const now = new Date();
      const startOfToday = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate()
      );
      const startOfYesterday = new Date(
        startOfToday.getTime() - 24 * 60 * 60 * 1000
      );
      const startOfWeek = new Date(
        startOfToday.getTime() - 7 * 24 * 60 * 60 * 1000
      );
      const groups: { [group: string]: HistoryItem[] } = {
        "Today": [],
        "Yesterday": [],
        "Last week": [],
        "Earlier": []
      };

      entries.forEach(entry => {
        const date = new Date(entry.accessedAt);
        let group = "";
        if (date >= startOfToday) {
          group = "Today";
        } else if (date >= startOfYesterday) {
          group = "Yesterday";
        } else if (date >= startOfWeek) {
          group = "Last week";
        } else {
          group = "Earlier";
        }
        groups[group].push({
          title: entry.calculatorTitle,
          link: `modify.html?id=${entry.calculatorId}`
        });
      });

      Object.entries(groups).forEach(([group, items]) => {
        if (items.length === 0) return;
        const secEl = document.createElement("div");
        secEl.className = "history-section";
        const header = document.createElement("div");
        header.className = "history-group";
        header.textContent = group;
        const list = document.createElement("ul");
        list.className = "history-list";

        items.forEach(item => {
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
    })
    .catch(error => {
      console.error("Error fetching history:", error);
    });
}