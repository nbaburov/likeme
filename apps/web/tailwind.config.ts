/* eslint-disable @typescript-eslint/no-require-imports */
import type { Config } from "tailwindcss";
import { mtConfig } from "@material-tailwind/react";

const config: Config = {
	content: [
		"./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
		"./src/components/**/*.{js,ts,jsx,tsx,mdx}",
		"./src/app/**/*.{js,ts,jsx,tsx,mdx}",
		"./node_modules/@material-tailwind/react/**/*.{js,ts,jsx,tsx}",
	],
	theme: {
		extend: {
			colors: {
				background: "var(--background)",
				foreground: "var(--foreground)",
				"likeme-primary": "#FF5757",
				"likeme-secondary": "#F2E9E9",
				"likeme-third": "#efeadb",
				"likeme-accent": "#E4593E",
				"likeme-text": "#282525",
			},
		},
	},
	plugins: [
		mtConfig({
			radius: "1.5rem",
			fonts: {
				sans: "Inter",
				mono: "Fira Code",
			},
			colors: {
				background: "#FFFFFF",
				foreground: "#4B5563",
				surface: {
					default: "#E5E7EB",
					dark: "#030712",
					light: "#F9FaFB",
					foreground: "#1F2937",
				},
				primary: {
					default: "#FF5757", // likeme-primary
					dark: "#E64C4C", // darker shade
					light: "#FF7070", // lighter shade
					foreground: "#F9FAFB",
				},
				secondary: {
					default: "#F2E9E9", // likeme-secondary
					dark: "#E8DFDF",
					light: "#F9F3F3",
					foreground: "#282525", // likeme-text
				},
				info: {
					default: "#2563EB",
					dark: "#1D4ED8",
					light: "#3B82F6",
					foreground: "#F9FAFB",
				},
				success: {
					default: "#16A34A",
					dark: "#15803D",
					light: "#22C55E",
					foreground: "#F9FAFB",
				},
				warning: {
					default: "#EAB308",
					dark: "#CA8A04",
					light: "#FACC15",
					foreground: "#282525",
				},
				error: {
					default: "#E4593E", // likeme-accent
					dark: "#D44B31",
					light: "#E87259",
					foreground: "#F9FAFB",
				},
			},
			darkColors: {
				background: "#030712",
				foreground: "#9CA3AF",
				surface: {
					default: "#1F2937",
					dark: "#F9FAFB",
					light: "#111827",
					foreground: "#E5E7EB",
				},
				primary: {
					default: "#FF5757",
					dark: "#E64C4C",
					light: "#FF7070",
					foreground: "#030712",
				},
				secondary: {
					default: "#F2E9E9",
					dark: "#E8DFDF",
					light: "#F9F3F3",
					foreground: "#282525",
				},
				info: {
					default: "#3B82F6",
					dark: "#60A5FA",
					light: "#2563EB",
					foreground: "#030712",
				},
				success: {
					default: "#22C55E",
					dark: "#16A34A",
					light: "#4ADE80",
					foreground: "#030712",
				},
				warning: {
					default: "#FACC15",
					dark: "#EABC08",
					light: "#FDE047",
					foreground: "#030712",
				},
				error: {
					default: "#E4593E",
					dark: "#D44B31",
					light: "#E87259",
					foreground: "#030712",
				},
			},
		}),
		require("@tailwindcss/forms"),
	],
};

export default config;
