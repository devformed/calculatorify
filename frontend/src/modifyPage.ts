import {initNavToggle} from './nav.js';
import {renderHistory} from './history.js';
import {initDashboardSearch} from './dashboardSearch.js';

// Token type from backend
interface Token {
    type: string;
    value: string;
}

// Evaluate postfix token list with variable map
function evaluatePostfix(tokens: Token[], variableMap: Record<string, string>): number {
    const stack: number[] = [];
    console.log(`Postfix tokens: ${JSON.stringify(tokens)}`);
    tokens.forEach((token, idx) => {
        console.log(
            `Token ${idx}: ${token.type}:${token.value} | Stack before: ${JSON.stringify(stack)}`
        );
        switch (token.type) {
            case 'DECIMAL_LITERAL':
                stack.push(parseFloat(token.value));
                break;
            case 'VARIABLE': {
                const key = token.value; // e.g. '${loan_amount}'
                const raw = variableMap[key];
                if (raw === undefined) throw new Error('Unknown variable ' + key);
                const num = parseFloat(raw);
                if (isNaN(num)) throw new Error('Variable not a number: ' + raw);
                stack.push(num);
                break;
            }
            case 'OPERATOR': {
                let b = stack.pop();
                let a = stack.pop();
                if (b === undefined) throw new Error('Insufficient operands');
                // handle unary operators: if no left operand, treat a as 0
                if (a === undefined) a = 0;
                let res: number;
                switch (token.value) {
                    case '+':
                        res = a + b;
                        break;
                    case '-':
                        res = a - b;
                        break;
                    case '*':
                        res = a * b;
                        break;
                    case '/':
                        res = a / b;
                        break;
                    case '^':
                        res = Math.pow(a, b);
                        break;
                    case '%':
                        res = a % b;
                        break;
                    default:
                        throw new Error('Unsupported operator ' + token.value);
                }
                stack.push(res);
                break;
            }
            case 'FUNCTION': {
                const fn = token.value;
                // pop arguments according to function
                switch (fn) {
                    case 'SQRT': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for SQRT');
                        stack.push(Math.sqrt(x));
                        break;
                    }
                    case 'POW': {
                        const exp = stack.pop();
                        const base = stack.pop();
                        if (base === undefined || exp === undefined) throw new Error('POW needs 2 args');
                        stack.push(Math.pow(base, exp));
                        break;
                    }
                    case 'LN': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for LN');
                        stack.push(Math.log(x));
                        break;
                    }
                    case 'LOG10': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for LOG10');
                        stack.push(Math.log10(x));
                        break;
                    }
                    case 'EXP': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for EXP');
                        stack.push(Math.exp(x));
                        break;
                    }
                    case 'SIN': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for SIN');
                        stack.push(Math.sin(x));
                        break;
                    }
                    case 'COS': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for COS');
                        stack.push(Math.cos(x));
                        break;
                    }
                    case 'TAN': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for TAN');
                        stack.push(Math.tan(x));
                        break;
                    }
                    case 'ABS': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for ABS');
                        stack.push(Math.abs(x));
                        break;
                    }
                    case 'ROUND': {
                        const x = stack.pop();
                        if (x === undefined) throw new Error('No arg for ROUND');
                        stack.push(Math.round(x));
                        break;
                    }
                    case 'ROUND_TO_N': {
                        const n = stack.pop();
                        const y = stack.pop();
                        if (y === undefined || n === undefined) throw new Error('ROUND_TO_N needs 2 args');
                        const factor = Math.pow(10, Math.trunc(n));
                        stack.push(Math.round(y * factor) / factor);
                        break;
                    }
                    case 'ROUND_UP_TO_N': {
                        const n = stack.pop();
                        const y = stack.pop();
                        if (y === undefined || n === undefined) throw new Error('ROUND_UP_TO_N needs 2 args');
                        const f = Math.pow(10, Math.trunc(n));
                        stack.push(Math.ceil(y * f) / f);
                        break;
                    }
                    case 'ROUND_DOWN_TO_N': {
                        const n = stack.pop();
                        const y = stack.pop();
                        if (y === undefined || n === undefined) throw new Error('ROUND_DOWN_TO_N needs 2 args');
                        const f = Math.pow(10, Math.trunc(n));
                        stack.push(Math.floor(y * f) / f);
                        break;
                    }
                    case 'MIN': {
                        const b2 = stack.pop();
                        const a2 = stack.pop();
                        if (a2 === undefined || b2 === undefined) throw new Error('MIN needs 2 args');
                        stack.push(Math.min(a2, b2));
                        break;
                    }
                    case 'MAX': {
                        const b2 = stack.pop();
                        const a2 = stack.pop();
                        if (a2 === undefined || b2 === undefined) throw new Error('MAX needs 2 args');
                        stack.push(Math.max(a2, b2));
                        break;
                    }
                    default:
                        throw new Error('Unsupported function ' + fn);
                }
                break;
            }
            default:
                throw new Error('Unsupported token type ' + token.type);
        }
        console.log(
            `Token ${idx} processed | Stack after: ${JSON.stringify(stack)}`
        );
    });
    console.log(`Final stack: ${JSON.stringify(stack)}`);
    if (stack.length !== 1) throw new Error('Invalid expression');
    return stack[0];
}

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

// Entry from GET /calculators/{id}, including metadata and postfix map
interface CalculatorEntry {
    id: string;
    title: string;
    description?: string;
    config: CalculatorConfig;
    createdAt: string;
    updatedAt: string;
    userId: string;
}

document.addEventListener('DOMContentLoaded', async () => {
    initNavToggle();
    renderHistory();
    initDashboardSearch();
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    const prompt = params.get('prompt');
    let card: CalculatorEntry;

    // Helper to show loader
    function showLoader() {
        const overlay = document.createElement('div');
        overlay.className = 'loader-overlay';
        overlay.innerHTML = '<div class="spinner"></div>';
        document.body.append(overlay);
        return overlay;
    }

    // Load entry by prompt or by id
    if (prompt) {
        // create from AI via JSON body
        const loader = showLoader();
        const resp = await fetch(`http://localhost:8080/calculators/construct`, {
            method: 'POST',
            credentials: 'include',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({prompt})
        });
        loader.remove();
        if (!resp.ok) {
            const txt = await resp.text();
            alert(`Construction failed: ${resp.status} ${txt}`);
            return;
        }
        card = await resp.json();
    } else if (id) {
        const resp = await fetch(`http://localhost:8080/calculators/${id}`, {method: 'GET', credentials: 'include'});
        if (!resp.ok) {
            console.error('Failed to load calculator:', resp.status);
            return;
        }
        card = await resp.json();
    } else {
        return;
    }
    // Entry loaded into 'card' (from /construct or GET)
    try {
        console.log(card);
        const titleEl = document.getElementById('modifyTitle') as HTMLElement;
        if (titleEl) {
            titleEl.textContent = card.title;
            // inline edit title
            titleEl.style.cursor = 'pointer';
            titleEl.addEventListener('dblclick', () => {
                titleEl.contentEditable = 'true';
                titleEl.focus();
            });
            titleEl.addEventListener('blur', () => {
                titleEl.contentEditable = 'false';
                card.title = titleEl.textContent || card.title;
            });
            titleEl.addEventListener('keydown', e => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    titleEl.blur();
                }
            });
        }
        const inputsContainer = document.getElementById('inputsContainer') as HTMLElement;
        const outputsContainer = document.getElementById('outputsContainer') as HTMLElement;
        const descriptionEl = document.getElementById('modifyDescription') as HTMLElement;
        if (!inputsContainer || !outputsContainer || !descriptionEl) return;
        // inline edit description
        descriptionEl.style.cursor = 'pointer';
        descriptionEl.addEventListener('dblclick', () => {
            descriptionEl.contentEditable = 'true';
            descriptionEl.focus();
        });
        descriptionEl.addEventListener('blur', () => {
            descriptionEl.contentEditable = 'false';
            card.description = descriptionEl.textContent || '';
        });
        descriptionEl.addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                e.preventDefault();
                descriptionEl.blur();
            }
        });
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
                        // inline edit input label
                        nameSpan.style.cursor = 'pointer';
                        nameSpan.addEventListener('click', () => {
                            nameSpan.contentEditable = 'true';
                            nameSpan.focus();
                        });
                        nameSpan.addEventListener('blur', () => {
                            nameSpan.contentEditable = 'false';
                            slider.name = nameSpan.textContent || slider.name;
                        });
                        nameSpan.addEventListener('keydown', e => {
                            if (e.key === 'Enter') {
                                e.preventDefault();
                                nameSpan.blur();
                            }
                        });
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
                                span.contentEditable = 'true';
                                span.focus();
                            });
                            span.addEventListener('blur', () => {
                                span.contentEditable = 'false';
                                const parts = span.textContent?.trim();
                                const val = parseFloat(parts || '');
                                if (!isNaN(val)) {
                                    span.textContent = String(val);
                                    if (span === minSpan) rangeInput.min = span.textContent;
                                    else rangeInput.max = span.textContent;
                                }
                            });
                            span.addEventListener('keydown', e => {
                                if (e.key === 'Enter') {
                                    e.preventDefault();
                                    span.blur();
                                }
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
                        const nameSpan2 = document.createElement('span');
                        nameSpan2.textContent = numIn.name;
                        nameSpan2.style.cursor = 'pointer';
                        nameSpan2.addEventListener('click', () => {
                            nameSpan2.contentEditable = 'true';
                            nameSpan2.focus();
                        });
                        nameSpan2.addEventListener('blur', () => {
                            nameSpan2.contentEditable = 'false';
                            numIn.name = nameSpan2.textContent || numIn.name;
                        });
                        nameSpan2.addEventListener('keydown', e => {
                            if (e.key === 'Enter') {
                                e.preventDefault();
                                nameSpan2.blur();
                            }
                        });
                        label.textContent = '';
                        label.append(nameSpan2);
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
                        const legendSpan = document.createElement('span');
                        legendSpan.textContent = rad.name;
                        legendSpan.style.cursor = 'pointer';
                        legendSpan.addEventListener('click', () => {
                            legendSpan.contentEditable = 'true';
                            legendSpan.focus();
                        });
                        legendSpan.addEventListener('blur', () => {
                            legendSpan.contentEditable = 'false';
                            rad.name = legendSpan.textContent || rad.name;
                        });
                        legendSpan.addEventListener('keydown', e => {
                            if (e.key === 'Enter') {
                                e.preventDefault();
                                legendSpan.blur();
                            }
                        });
                        legend.textContent = '';
                        legend.append(legendSpan);
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
        // Keep track of postfix formulas for each output name
        const postfixMap: Record<string, Array<{ type: string; value: string }>> =
            (card as any).outputNameToPostfixFormula;
        if (postfixMap && Object.keys(postfixMap).length) {
            const sep = document.createElement('div');
            sep.className = 'dashboard-card-outcome-label';
            sep.textContent = 'Outcome';
            outputsContainer.append(sep);
            Object.keys(postfixMap)
                .sort((a, b) => {
                    const oa = card.config.outputs.find(o => o.name === a)!;
                    const ob = card.config.outputs.find(o => o.name === b)!;
                    return oa.order - ob.order;
                })
                .forEach(name => {
                    const outEl = document.createElement('div');
                    outEl.className = 'dashboard-card-output';
                    outEl.setAttribute('data-output-name', name);
                    const labelEl = document.createElement('span');
                    labelEl.className = 'dashboard-card-output-label';
                    labelEl.textContent = name;
                    // inline edit output name
                    labelEl.style.cursor = 'pointer';
                    const outObj = card.config.outputs.find(o => o.name === name)!;
                    labelEl.addEventListener('click', () => {
                        labelEl.contentEditable = 'true';
                        labelEl.focus();
                    });
                    labelEl.addEventListener('blur', () => {
                        labelEl.contentEditable = 'false';
                        outObj.name = labelEl.textContent || outObj.name;
                    });
                    labelEl.addEventListener('keydown', e => {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            labelEl.blur();
                        }
                    });
                    const valueEl = document.createElement('span');
                    valueEl.className = 'dashboard-card-output-value';
                    valueEl.textContent = ''; // will be computed
                    outEl.append(labelEl, valueEl);
                    outputsContainer.append(outEl);
                });
        }
        // Description
        descriptionEl.textContent = card.description || '';

        // Function to compute all outputs
        function computeOutputs() {
            // build variable map
            const vars: Record<string, string> = {};
            card.config.inputs.forEach(inp => {
                if (inp.type === 'SLIDER' || inp.type === 'NUMBER') {
                    const el = document.getElementById(`${card.id}-${inp.id}`) as HTMLInputElement;
                    // map variable token '${id}' to current value
                    const varKey = '${' + inp.id + '}';
                    vars[varKey] = el.value;
                }
            });
            // for each output
            if (postfixMap) {
                Object.entries(postfixMap).forEach(([name, tokens]) => {
                    const outEl = outputsContainer.querySelector(`div[data-output-name="${name}"] .dashboard-card-output-value`);
                    if (!outEl) return;
                    try {
                        const result = evaluatePostfix(tokens, vars);
                        // Apply configured precision for output if present
                        const outConfig = card.config.outputs.find(o => o.name === name);
                        const text = outConfig && typeof outConfig.precision === 'number'
                            ? result.toFixed(outConfig.precision)
                            : String(result);
                        (outEl as HTMLElement).textContent = text;
                    } catch (err) {
                        (outEl as HTMLElement).textContent = '#ERROR';
                    }
                });
            }
        }

        // Attach listeners
        computeOutputs();
        // on input change, recompute outputs
        card.config.inputs.forEach(inp => {
            const el = document.getElementById(`${card.id}-${inp.id}`);
            if (el) el.addEventListener('input', computeOutputs);
        });
        // Save changes button
        const saveBtn = document.getElementById('saveChanges') as HTMLButtonElement;
        const deleteBtn = document.getElementById('deleteCalculator') as HTMLButtonElement;
        if (deleteBtn) {
            deleteBtn.addEventListener('click', async () => {
                if (!confirm('Are you sure you want to delete this calculator?')) return;
                try {
                    const resp = await fetch(`http://localhost:8080/calculators/${card.id}`, {
                        method: 'DELETE',
                        credentials: 'include'
                    });
                    if (!resp.ok) {
                        const text = await resp.text();
                        alert(`Delete failed: ${resp.status} ${text}`);
                    } else {
                        alert('Deleted successfully');
                        window.location.href = 'dashboard.html';
                    }
                } catch (err) {
                    console.error('Error deleting:', err);
                    alert('Error deleting calculator');
                }
            });
        }
        if (saveBtn) {
            saveBtn.addEventListener('click', async () => {
                try {
                    // Build full payload (id undefined for new entries)
                    const payload = {
                        id: card.id,
                        title: card.title,
                        description: descriptionEl.textContent,
                        config: card.config,
                        createdAt: card.createdAt,
                        updatedAt: card.updatedAt,
                        userId: card.userId
                    };
                    let resp;
                    // Decide POST (create) vs PUT (update)
                    if (card.id) {
                        resp = await fetch(`http://localhost:8080/calculators/${card.id}`, {
                            method: 'PUT',
                            credentials: 'include',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(payload)
                        });
                    } else {
                        resp = await fetch(`http://localhost:8080/calculators`, {
                            method: 'POST',
                            credentials: 'include',
                            headers: {'Content-Type': 'application/json'},
                            body: JSON.stringify(payload)
                        });
                    }
                    if (!resp.ok) {
                        const text = await resp.text();
                        alert(`Save failed: ${resp.status} ${text}`);
                        return;
                    }
                    // On create, server returns new id
                    if (!card.id) {
                        const newId = await resp.json();
                        card.id = newId;
                        // Redirect to edit page with new id
                        window.location.href = `${window.location.pathname}?id=${newId}`;
                    } else {
                        alert('Saved successfully');
                    }
                } catch (err) {
                    console.error('Error saving:', err);
                    alert('Error saving changes');
                }
            });
        }
    } catch (err) {
        console.error('Error loading calculator:', err);
    }
});