// Types for dynamic calculator inputs
interface SliderInput {
  type: 'SLIDER';
  id: string;
  name: string;
  minValue: number;
  maxValue: number;
  step: number;
  order: number;
}
interface NumberInput {
  type: 'NUMBER';
  id: string;
  name: string;
  number: number;
  precision: number;
  order: number;
}
interface RadioButtonsInput {
  type: 'RADIO_BUTTONS';
  id: string;
  name: string;
  nameValueOptions: Record<string, number>;
  order: number;
}
type CalculatorInput = SliderInput | NumberInput | RadioButtonsInput;
interface CalculatorConfig {
  inputs: CalculatorInput[];
}
interface CalculatorEntry {
  id: string;
  title: string;
  description?: string;
  config: CalculatorConfig;
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
    const cards: CalculatorEntry[] = await response.json();
    cards.forEach((card) => {
      const { id, title, description, config } = card;
      const cardEl = document.createElement("section");
      cardEl.className = "dashboard-card";
      const heading = document.createElement("h2");
      heading.textContent = title;
      cardEl.append(heading);
      if (description) {
        const desc = document.createElement("p");
        desc.textContent = description;
        cardEl.append(desc);
      }
      // Render dynamic inputs (sorted by order)
      [...config.inputs]
        .sort((a, b) => a.order - b.order)
        .forEach((input) => {
        const wrapper = document.createElement("div");
        wrapper.className = "dashboard-card-input";
        if (input.type === "SLIDER") {
          const sliderInput = input as SliderInput;
          const label = document.createElement("label");
          label.htmlFor = `${id}-${sliderInput.id}`;
          const valueSpan = document.createElement("span");
          valueSpan.textContent = String(sliderInput.minValue);
          label.textContent = `${sliderInput.name}: `;
          label.append(valueSpan);
          const range = document.createElement("input");
          range.type = "range";
          range.id = `${id}-${sliderInput.id}`;
          range.min = String(sliderInput.minValue);
          range.max = String(sliderInput.maxValue);
          range.step = String(sliderInput.step);
          range.value = String(sliderInput.minValue);
          range.addEventListener("input", () => {
            valueSpan.textContent = range.value;
          });
          wrapper.append(label, range);
        } else if (input.type === "NUMBER") {
          const numberInput = input as NumberInput;
          const label = document.createElement("label");
          label.htmlFor = `${id}-${numberInput.id}`;
          label.textContent = numberInput.name;
          const num = document.createElement("input");
          num.type = "number";
          num.id = `${id}-${numberInput.id}`;
          num.value = String(numberInput.number);
          num.step = String(1 / Math.pow(10, numberInput.precision));
          wrapper.append(label, num);
        } else if (input.type === "RADIO_BUTTONS") {
          const radioInput = input as RadioButtonsInput;
          const fieldset = document.createElement("fieldset");
          const legend = document.createElement("legend");
          legend.textContent = radioInput.name;
          fieldset.append(legend);
          Object.entries(radioInput.nameValueOptions).forEach(([optName, optValue], idx) => {
            const optionId = `${id}-${radioInput.id}-${idx}`;
            const radio = document.createElement("input");
            radio.type = "radio";
            radio.name = `${id}-${radioInput.id}`;
            radio.id = optionId;
            radio.value = String(optValue);
            if (idx === 0) radio.checked = true;
            const label = document.createElement("label");
            label.htmlFor = optionId;
            label.textContent = optName;
            fieldset.append(radio, label);
          });
          wrapper.append(fieldset);
        }
        cardEl.append(wrapper);
      });
      // Calculate button
      const btn = document.createElement("button");
      btn.className = "btn btn-primary";
      btn.textContent = "Calculate";
      btn.addEventListener("click", () => {
        // TODO: implement calculation logic
        alert(`Calculate ${title}`);
      });
      cardEl.append(btn);
      container.append(cardEl);
    });
  } catch (error) {
    console.error("Error fetching calculator cards:", error);
  }
}