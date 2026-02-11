import { useEffect, useMemo, useState } from "react";
import { Pressable, StyleSheet, Text, View, useColorScheme, Platform } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { Ionicons } from "@expo/vector-icons";
import {
  appendDecimal,
  appendDigit,
  appendOperator,
  deleteLast,
  evaluateExpression,
  formatExpression,
  isExpressionComplete,
  STORAGE_KEY,
} from "../lib/calculator";

const MAX_INPUT_LENGTH = 24;

type ButtonVariant = "number" | "operator" | "action" | "equals";
type ButtonConfig = {
  label?: string;
  icon?: keyof typeof Ionicons.glyphMap;
  onPress: () => void;
  variant: ButtonVariant;
  flex?: number;
  accessibilityLabel: string;
};

export default function Index() {
  const scheme = useColorScheme();
  const [expression, setExpression] = useState("");
  const [result, setResult] = useState("0");
  const [error, setError] = useState("");
  const [justEvaluated, setJustEvaluated] = useState(false);

  const theme = useMemo(
    () =>
      scheme === "dark"
        ? {
            background: "#0b0f19",
            surface: "#121826",
            textPrimary: "#f8fafc",
            textSecondary: "#94a3b8",
            accent: "#60a5fa",
            operator: "#1e293b",
            operatorText: "#f8fafc",
            danger: "#fb7185",
            border: "#1f2937",
            shadow: "rgba(0,0,0,0.4)",
          }
        : {
            background: "#f4f5f7",
            surface: "#ffffff",
            textPrimary: "#0f172a",
            textSecondary: "#475569",
            accent: "#2563eb",
            operator: "#e2e8f0",
            operatorText: "#0f172a",
            danger: "#ef4444",
            border: "#e2e8f0",
            shadow: "rgba(15, 23, 42, 0.08)",
          },
    [scheme]
  );

  useEffect(() => {
    const loadState = async () => {
      try {
        const saved = await AsyncStorage.getItem(STORAGE_KEY);
        if (saved) {
          const parsed = JSON.parse(saved) as {
            expression?: string;
            result?: string;
            error?: string;
            justEvaluated?: boolean;
          };
          setExpression(parsed.expression ?? "");
          setResult(parsed.result ?? "0");
          setError(parsed.error ?? "");
          setJustEvaluated(parsed.justEvaluated ?? false);
        }
      } catch {
        setExpression("");
        setResult("0");
        setError("");
        setJustEvaluated(false);
      }
    };

    loadState();
  }, []);

  useEffect(() => {
    const persistState = async () => {
      try {
        await AsyncStorage.setItem(
          STORAGE_KEY,
          JSON.stringify({ expression, result, error, justEvaluated })
        );
      } catch {
        // Persistence failures should not block usage.
      }
    };

    persistState();
  }, [expression, result, error, justEvaluated]);

  useEffect(() => {
    if (!expression) {
      setResult("0");
      setError("");
      return;
    }

    if (!isExpressionComplete(expression)) {
      setError("");
      return;
    }

    const evaluation = evaluateExpression(expression);
    if (evaluation.error) {
      setError(evaluation.error);
      return;
    }
    if (evaluation.result !== null) {
      setError("");
      setResult(evaluation.result);
    }
  }, [expression]);

  const resetError = () => {
    if (error) {
      setError("");
    }
  };

  const handleDigit = (digit: string) => {
    resetError();
    setJustEvaluated(false);
    setExpression((prev) => {
      const base = justEvaluated ? "" : prev;
      if (base.length >= MAX_INPUT_LENGTH) {
        return base;
      }
      return appendDigit(base, digit);
    });
  };

  const handleDecimal = () => {
    resetError();
    setJustEvaluated(false);
    setExpression((prev) => {
      const base = justEvaluated ? "" : prev;
      if (base.length >= MAX_INPUT_LENGTH) {
        return base;
      }
      return appendDecimal(base);
    });
  };

  const handleOperator = (operator: string) => {
    resetError();
    setJustEvaluated(false);
    setExpression((prev) => {
      const base = prev || (justEvaluated ? result : "");
      return appendOperator(base, operator);
    });
  };

  const handleClear = () => {
    setExpression("");
    setResult("0");
    setError("");
    setJustEvaluated(false);
  };

  const handleDelete = () => {
    resetError();
    setJustEvaluated(false);
    setExpression((prev) => deleteLast(prev));
  };

  const handleEquals = () => {
    if (!expression) {
      return;
    }

    const evaluation = evaluateExpression(expression);
    if (evaluation.error) {
      setError(evaluation.error);
      return;
    }
    if (evaluation.result !== null) {
      setError("");
      setExpression(evaluation.result);
      setResult(evaluation.result);
      setJustEvaluated(true);
    }
  };

  const buttons: ButtonConfig[][] = [
    [
      {
        label: "C",
        onPress: handleClear,
        variant: "action",
        accessibilityLabel: "Clear",
      },
      {
        icon: "backspace-outline",
        onPress: handleDelete,
        variant: "action",
        accessibilityLabel: "Delete",
      },
      {
        label: "÷",
        onPress: () => handleOperator("÷"),
        variant: "operator",
        accessibilityLabel: "Divide",
      },
      {
        label: "×",
        onPress: () => handleOperator("×"),
        variant: "operator",
        accessibilityLabel: "Multiply",
      },
    ],
    [
      {
        label: "7",
        onPress: () => handleDigit("7"),
        variant: "number",
        accessibilityLabel: "7",
      },
      {
        label: "8",
        onPress: () => handleDigit("8"),
        variant: "number",
        accessibilityLabel: "8",
      },
      {
        label: "9",
        onPress: () => handleDigit("9"),
        variant: "number",
        accessibilityLabel: "9",
      },
      {
        label: "-",
        onPress: () => handleOperator("-"),
        variant: "operator",
        accessibilityLabel: "Subtract",
      },
    ],
    [
      {
        label: "4",
        onPress: () => handleDigit("4"),
        variant: "number",
        accessibilityLabel: "4",
      },
      {
        label: "5",
        onPress: () => handleDigit("5"),
        variant: "number",
        accessibilityLabel: "5",
      },
      {
        label: "6",
        onPress: () => handleDigit("6"),
        variant: "number",
        accessibilityLabel: "6",
      },
      {
        label: "+",
        onPress: () => handleOperator("+"),
        variant: "operator",
        accessibilityLabel: "Add",
      },
    ],
    [
      {
        label: "1",
        onPress: () => handleDigit("1"),
        variant: "number",
        accessibilityLabel: "1",
      },
      {
        label: "2",
        onPress: () => handleDigit("2"),
        variant: "number",
        accessibilityLabel: "2",
      },
      {
        label: "3",
        onPress: () => handleDigit("3"),
        variant: "number",
        accessibilityLabel: "3",
      },
      {
        label: "=",
        onPress: handleEquals,
        variant: "equals",
        accessibilityLabel: "Equals",
      },
    ],
    [
      {
        label: "0",
        onPress: () => handleDigit("0"),
        variant: "number",
        flex: 2,
        accessibilityLabel: "0",
      },
      {
        label: ".",
        onPress: handleDecimal,
        variant: "number",
        accessibilityLabel: "Decimal",
      },
    ],
  ];

  const buttonStyles = useMemo(
    () => ({
      number: { backgroundColor: theme.surface, color: theme.textPrimary },
      operator: { backgroundColor: theme.operator, color: theme.operatorText },
      action: { backgroundColor: theme.operator, color: theme.danger },
      equals: { backgroundColor: theme.accent, color: "#ffffff" },
    }),
    [theme]
  );

  return (
    <SafeAreaView style={[styles.container, { backgroundColor: theme.background }]}>
      <View style={styles.inner}>
        <View style={styles.display}>
          <Text
            style={[styles.expressionText, { color: theme.textSecondary }]}
            numberOfLines={1}
            adjustsFontSizeToFit
          >
            {formatExpression(expression)}
          </Text>
          <Text
            style={[
              styles.resultText,
              { color: error ? theme.danger : theme.textPrimary },
            ]}
            numberOfLines={1}
            adjustsFontSizeToFit
          >
            {error || result}
          </Text>
        </View>

        <View style={styles.keypad}>
          {buttons.map((row, rowIndex) => (
            <View key={`row-${rowIndex}`} style={styles.row}>
              {row.map((button, index) => {
                const variantStyle = buttonStyles[button.variant];
                return (
                  <Pressable
                    key={`${button.accessibilityLabel}-${index}`}
                    accessibilityRole="button"
                    accessibilityLabel={button.accessibilityLabel}
                    android_ripple={{ color: theme.border }}
                    onPress={button.onPress}
                    style={({ pressed }) => [
                      styles.button,
                      { flex: button.flex ?? 1, backgroundColor: variantStyle.backgroundColor },
                      styles.buttonShadow,
                      pressed && styles.buttonPressed,
                    ]}
                  >
                    {button.icon ? (
                      <Ionicons name={button.icon} size={22} color={variantStyle.color} />
                    ) : (
                      <Text style={[styles.buttonText, { color: variantStyle.color }]}>
                        {button.label}
                      </Text>
                    )}
                  </Pressable>
                );
              })}
            </View>
          ))}
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  inner: {
    flex: 1,
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  display: {
    flex: 0.4,
    justifyContent: "flex-end",
    alignItems: "flex-end",
    paddingTop: 20,
    paddingBottom: 16,
  },
  expressionText: {
    fontSize: 22,
    fontWeight: "500",
    marginBottom: 8,
  },
  resultText: {
    fontSize: 40,
    fontWeight: "700",
  },
  keypad: {
    flex: 0.6,
    justifyContent: "flex-end",
    gap: 12,
  },
  row: {
    flexDirection: "row",
    gap: 12,
  },
  button: {
    minHeight: 64,
    borderRadius: 18,
    alignItems: "center",
    justifyContent: "center",
  },
  buttonText: {
    fontSize: 24,
    fontWeight: "600",
  },
  buttonPressed: {
    opacity: 0.65,
    transform: [{ scale: 0.98 }],
  },
  buttonShadow: Platform.select({
    ios: {
      shadowColor: "#000",
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.12,
      shadowRadius: 6,
    },
    android: {
      elevation: 2,
    },
    default: {},
  }),
});

