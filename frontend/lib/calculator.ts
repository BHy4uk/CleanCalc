export const OPERATORS = ["+", "-", "×", "÷"] as const;

const operatorSet = new Set(OPERATORS);
export const STORAGE_KEY = "cleancalc_state";

export function isOperator(token: string): boolean {
  return operatorSet.has(token as (typeof OPERATORS)[number]);
}

export function formatExpression(expression: string): string {
  if (!expression) {
    return "0";
  }

  return expression
    .replace(/([+\-×÷])/g, " $1 ")
    .replace(/\s+/g, " ")
    .trim();
}

function getLastNumberSegment(expression: string): string {
  const parts = expression.split(/[+\-×÷]/);
  return parts[parts.length - 1] ?? "";
}

function isNumberToken(token: string): boolean {
  return token !== "" && !Number.isNaN(Number(token));
}

export function appendDigit(expression: string, digit: string): string {
  if (!/^\d$/.test(digit)) {
    return expression;
  }

  const currentSegment = getLastNumberSegment(expression);
  if (currentSegment === "0") {
    const prefix = expression.slice(0, expression.length - currentSegment.length);
    return `${prefix}${digit}`;
  }

  return `${expression}${digit}`;
}

export function appendDecimal(expression: string): string {
  const lastChar = expression.slice(-1);
  const currentSegment = getLastNumberSegment(expression);
  if (currentSegment.includes(".")) {
    return expression;
  }

  if (!expression || isOperator(lastChar)) {
    return `${expression}0.`;
  }

  return `${expression}.`;
}

export function appendOperator(expression: string, operator: string): string {
  if (!isOperator(operator)) {
    return expression;
  }

  if (!expression) {
    return operator === "-" ? "-" : "";
  }

  const lastChar = expression.slice(-1);
  if (isOperator(lastChar)) {
    if (lastChar === operator) {
      return expression;
    }
    return `${expression.slice(0, -1)}${operator}`;
  }

  return `${expression}${operator}`;
}

export function deleteLast(expression: string): string {
  return expression.slice(0, -1);
}

export function sanitizeExpression(expression: string): string {
  let sanitized = expression;
  while (sanitized && isOperator(sanitized.slice(-1))) {
    sanitized = sanitized.slice(0, -1);
  }

  if (sanitized.endsWith(".")) {
    sanitized = sanitized.slice(0, -1);
  }

  return sanitized;
}

export function isExpressionComplete(expression: string): boolean {
  if (!expression) {
    return false;
  }

  const lastChar = expression.slice(-1);
  return !isOperator(lastChar) && lastChar !== ".";
}

export function evaluateExpression(expression: string): {
  result: string | null;
  error?: string;
} {
  const sanitized = sanitizeExpression(expression);
  if (!sanitized) {
    return { result: null };
  }

  const rawTokens = sanitized.match(/(\d*\.\d+|\d+\.?\d*|[+\-×÷])/g);
  if (!rawTokens || rawTokens.length === 0) {
    return { result: null };
  }

  const tokens = rawTokens.map((token) => {
    if (token === "×") {
      return "*";
    }
    if (token === "÷") {
      return "/";
    }
    return token;
  });

  if (!isNumberToken(tokens[0]) || !isNumberToken(tokens[tokens.length - 1])) {
    return { result: null };
  }

  const firstPass: string[] = [];
  for (let i = 0; i < tokens.length; i += 1) {
    const token = tokens[i];
    if (token === "*" || token === "/") {
      const prevToken = firstPass.pop();
      const nextToken = tokens[i + 1];
      if (!prevToken || !nextToken || !isNumberToken(prevToken) || !isNumberToken(nextToken)) {
        return { result: null };
      }

      const prevValue = Number(prevToken);
      const nextValue = Number(nextToken);
      if (token === "/" && nextValue === 0) {
        return { result: null, error: "Cannot divide by zero" };
      }

      const computed = token === "*" ? prevValue * nextValue : prevValue / nextValue;
      if (!Number.isFinite(computed)) {
        return { result: null, error: "Invalid input" };
      }

      firstPass.push(String(computed));
      i += 1;
    } else {
      firstPass.push(token);
    }
  }

  let total = Number(firstPass[0]);
  if (Number.isNaN(total)) {
    return { result: null };
  }

  for (let i = 1; i < firstPass.length; i += 2) {
    const operator = firstPass[i];
    const nextToken = firstPass[i + 1];
    if (!nextToken || !isNumberToken(nextToken)) {
      return { result: null };
    }

    const nextValue = Number(nextToken);
    if (operator === "+") {
      total += nextValue;
    } else if (operator === "-") {
      total -= nextValue;
    }
  }

  if (!Number.isFinite(total)) {
    return { result: null, error: "Invalid input" };
  }

  return { result: formatResult(total) };
}

export function formatResult(value: number): string {
  const rounded = Math.round(value * 1e10) / 1e10;
  let text = rounded.toString();

  if (text.includes("e")) {
    text = rounded.toPrecision(10);
  }

  if (text.includes(".")) {
    text = text.replace(/\.?0+$/, "");
  }

  return text;
}
