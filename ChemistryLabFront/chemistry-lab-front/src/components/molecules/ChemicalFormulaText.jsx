function ChemicalFormulaText({ value, className = "" }) {
    const tokens = tokenizeChemicalText(value);

    return (
        <span className={`chemical-formula-text ${className}`.trim()}>
            {tokens.map((token, index) => renderToken(token, index))}
        </span>
    );
}

function renderToken(token, index) {
    if (token.type === "sub") {
        return <sub key={index}>{token.value}</sub>;
    }

    if (token.type === "sup") {
        return <sup key={index}>{token.value}</sup>;
    }

    if (token.type === "separator") {
        return <span key={index} className="chemical-separator"> {token.value} </span>;
    }

    return <span key={index}>{token.value}</span>;
}

function tokenizeChemicalText(value) {
    const text = String(value || "").trim();
    const tokens = [];

    let i = 0;
    let afterElementOrGroup = false;
    let atTermStart = true;

    while (i < text.length) {
        const char = text[i];

        if (char === " ") {
            i++;
            continue;
        }

        if (char === "+") {
            tokens.push({ type: "separator", value: "+" });
            i++;
            afterElementOrGroup = false;
            atTermStart = true;
            continue;
        }

        if (char === "-") {
            tokens.push({ type: "sup", value: "−" });
            i++;
            afterElementOrGroup = false;
            atTermStart = false;
            continue;
        }

        if (isDigit(char)) {
            const start = i;
            while (i < text.length && isDigit(text[i])) {
                i++;
            }

            const number = text.slice(start, i);
            const next = text[i];

            if (atTermStart && isElementStart(next)) {
                tokens.push({ type: "text", value: number });
            } else if (afterElementOrGroup) {
                tokens.push({ type: "sub", value: number });
            } else {
                tokens.push({ type: "text", value: number });
            }

            atTermStart = false;
            continue;
        }

        if (isElementStart(char)) {
            const start = i;
            i++;
            if (i < text.length && isLowercase(text[i])) {
                i++;
            }
            tokens.push({ type: "text", value: text.slice(start, i) });
            afterElementOrGroup = true;
            atTermStart = false;
            continue;
        }

        if (char === "(" || char === "[" || char === ")" || char === "]") {
            tokens.push({ type: "text", value: char });
            afterElementOrGroup = char === ")" || char === "]";
            atTermStart = false;
            i++;
            continue;
        }

        if (isChargeSymbol(char)) {
            tokens.push({ type: "sup", value: char });
            afterElementOrGroup = false;
            atTermStart = false;
            i++;
            continue;
        }

        tokens.push({ type: "text", value: char });
        afterElementOrGroup = false;
        atTermStart = false;
        i++;
    }

    return mergeChargeTokens(tokens);
}

function mergeChargeTokens(tokens) {
    const result = [];

    for (let i = 0; i < tokens.length; i++) {
        const current = tokens[i];
        const next = tokens[i + 1];

        if (current.type === "sub" && next?.type === "sup" && isOnlyDigits(current.value)) {
            result.push({ type: "sup", value: current.value + next.value });
            i++;
            continue;
        }

        result.push(current);
    }

    return result;
}

function isDigit(char) {
    return /[0-9]/.test(char);
}

function isOnlyDigits(value) {
    return /^[0-9]+$/.test(value);
}

function isElementStart(char) {
    return /[A-Z]/.test(char);
}

function isLowercase(char) {
    return /[a-z]/.test(char);
}

function isChargeSymbol(char) {
    return char === "⁺" || char === "⁻" || char === "+" || char === "−";
}

export default ChemicalFormulaText;
