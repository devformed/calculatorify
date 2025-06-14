import { initNavToggle } from './nav.js';
import { renderHistory } from './history.js';
import { initDashboardSearch } from './dashboardSearch.js';

// Types for dynamic inputs
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
// Output type with display name
interface CalculatorOutput {
  name: string;
  formula: string;
  precision: number;
  order: number;
}
interface CalculatorConfig {
  inputs: CalculatorInput[];
  outputs: CalculatorOutput[];
}
interface CalculatorEntry {
  id: string;
  title: string;
  description?: string;
  config: CalculatorConfig;
}

document.addEventListener('DOMContentLoaded', async () => {
  initNavToggle();
  renderHistory();
  initDashboardSearch();
  const params = new URLSearchParams(window.location.search);
  const id = params.get('id');
  if (!id) return;
  try {
    const resp = await fetch(`http://localhost:8080/calculators/${id}`, {
      method: 'GET',
      credentials: 'include'
    });
    if (!resp.ok) {
      console.error('Failed to load calculator:', resp.status);
      return;
    }
    const card: CalculatorEntry = await resp.json();
    const titleEl = document.getElementById('modifyTitle');
    if (titleEl) titleEl.textContent = card.title;
    const inputsContainer = document.getElementById('inputsContainer');
    const outputsContainer = document.getElementById('outputsContainer');
    const descriptionEl = document.getElementById('modifyDescription');
    if (!inputsContainer || !outputsContainer || !descriptionEl) return;
    // Inputs section
    if (card.config.inputs && card.config.inputs.length) {
      const inpLabel = document.createElement('div');
      inpLabel.className = 'dashboard-card-inputs-label';
      inpLabel.textContent = 'Input';
      inputsContainer.append(inpLabel);
      [...card.config.inputs]
        .sort((a, b) => a.order - b.order)
        .forEach(input => {
          const wrapper = document.createElement('div');
          wrapper.className = 'dashboard-card-input';
          if (input.type === 'SLIDER') {
            const slider = input as SliderInput;
            const min = slider.minValue;
            const max = slider.maxValue;
            const step = slider.step;
            // Label with editable min and max fields
            const label = document.createElement('label');
            label.htmlFor = `${card.id}-${slider.id}`;
            label.className = 'modify-slider-label';
            // Create label with name and range group
            const nameSpan = document.createElement('span');
            nameSpan.textContent = slider.name;
            const minSpan = document.createElement('span');
            minSpan.className = 'slider-range-min';
            minSpan.textContent = String(min);
            const dashSpan = document.createElement('span');
            dashSpan.className = 'slider-range-dash';
            dashSpan.textContent = '-';
            const maxSpan = document.createElement('span');
            maxSpan.className = 'slider-range-max';
            maxSpan.textContent = String(max);
            // Wrap min-dash-max in a group for correct alignment
            const rangeGroup = document.createElement('span');
            rangeGroup.className = 'slider-range-group';
            rangeGroup.append(minSpan, dashSpan, maxSpan);
            // Make min/max editable on click
            [minSpan, maxSpan].forEach(span => {
              span.style.cursor = 'pointer';
              span.addEventListener('click', () => {
                const initial = parseFloat(span.textContent || '');
                const input = document.createElement('input');
                input.type = 'number';
                input.value = String(initial);
                input.className = 'slider-edit-input';
                span.replaceWith(input);
                input.focus();
                input.select();
                function commit() {
                  const val = parseFloat(input.value);
                  if (!isNaN(val)) {
                    span.textContent = String(val);
                    if (span === minSpan) rangeInput.min = span.textContent;
                    else rangeInput.max = span.textContent;
                  }
                  input.replaceWith(span);
                }
                input.addEventListener('blur', commit);
                input.addEventListener('keydown', e => { if (e.key === 'Enter') commit(); });
              });
            });
            label.append(nameSpan, rangeGroup);
            wrapper.append(label);
            // Slider input
            const rangeInput = document.createElement('input');
            rangeInput.type = 'range';
            rangeInput.id = `${card.id}-${slider.id}`;
            rangeInput.min = String(min);
            rangeInput.max = String(max);
            rangeInput.step = String(step);
            // Random initial value
            const stepsCount = Math.floor((max - min) / step);
            const randomStep = Math.floor(Math.random() * (stepsCount + 1));
            const initVal = min + randomStep * step;
            rangeInput.value = String(initVal);
            // Slider and current value row
            const sliderRow = document.createElement('div');
            sliderRow.className = 'modify-slider-row';
            sliderRow.append(rangeInput);
            // Display current slider value
            const valueSpan = document.createElement('span');
            valueSpan.className = 'slider-value';
            valueSpan.textContent = rangeInput.value;
            sliderRow.append(valueSpan);
            wrapper.append(sliderRow);
            // Update on input
            rangeInput.addEventListener('input', () => {
              valueSpan.textContent = rangeInput.value;
            });
          } else if (input.type === 'NUMBER') {
            const numIn = input as NumberInput;
            const label = document.createElement('label');
            label.htmlFor = `${card.id}-${numIn.id}`;
            label.textContent = numIn.name;
            const numEl = document.createElement('input');
            numEl.type = 'number';
            numEl.id = `${card.id}-${numIn.id}`;
            numEl.value = String(numIn.number);
            numEl.step = String(1 / Math.pow(10, numIn.precision));
            wrapper.append(label, numEl);
          } else if (input.type === 'RADIO_BUTTONS') {
            const rad = input as RadioButtonsInput;
            const fieldset = document.createElement('fieldset');
            const legend = document.createElement('legend');
            legend.textContent = rad.name;
            fieldset.append(legend);
            Object.entries(rad.nameValueOptions).forEach(([optName, optVal], idx) => {
              const optionId = `${card.id}-${rad.id}-${idx}`;
              const radio = document.createElement('input');
              radio.type = 'radio';
              radio.name = `${card.id}-${rad.id}`;
              radio.id = optionId;
              radio.value = String(optVal);
              if (idx === 0) radio.checked = true;
              const lab = document.createElement('label');
              lab.htmlFor = optionId;
              lab.textContent = optName;
              fieldset.append(radio, lab);
            });
            wrapper.append(fieldset);
          }
          inputsContainer.append(wrapper);
        });
    }
    // Outputs section
    if (card.config.outputs && card.config.outputs.length) {
      const sep = document.createElement('div');
      sep.className = 'dashboard-card-outcome-label';
      sep.textContent = 'Outcome';
      outputsContainer.append(sep);
      [...card.config.outputs]
        .sort((a, b) => a.order - b.order)
        .forEach(output => {
          const outEl = document.createElement('div');
          outEl.className = 'dashboard-card-output';
          const labelEl = document.createElement('span');
          labelEl.className = 'dashboard-card-output-label';
          labelEl.textContent = output.name;
          const valueEl = document.createElement('span');
          valueEl.className = 'dashboard-card-output-value';
          valueEl.textContent = 'Hidden';
          outEl.append(labelEl, valueEl);
          outputsContainer.append(outEl);
        });
    }
    // Description
    descriptionEl.textContent = card.description || '';
  } catch (err) {
    console.error('Error loading calculator:', err);
  }
});