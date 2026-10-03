"use client";

import { useState } from "react";
import { BubbleButton } from "@components/ui/BubbleButton";
import { Input } from "@components/ui/Input";

interface SetupFormProps {
	onSubmit: (password: string, confirmPassword: string) => Promise<void>;
	error?: string;
	isLoading: boolean;
}

export function SetupForm({ onSubmit, error, isLoading }: SetupFormProps) {
	const [password, setPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const [validationError, setValidationError] = useState("");

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setValidationError("");

		if (password !== confirmPassword) {
			setValidationError("Passwords do not match");
			return;
		}

		if (password.length < 8) {
			setValidationError("Password must be at least 8 characters long");
			return;
		}

		await onSubmit(password, confirmPassword);
	};

	const displayError = validationError || error;

	return (
		<form onSubmit={handleSubmit} className="w-full space-y-4 p-12">
			<div>
				<Input
					type="password"
					placeholder="Enter your password"
					value={password}
					onChange={(e) => setPassword(e.target.value)}
					required
					label="Password"
					name="password"
					disabled={isLoading}
				/>
			</div>
			<div>
				<Input
					type="password"
					placeholder="Confirm your password"
					value={confirmPassword}
					onChange={(e) => setConfirmPassword(e.target.value)}
					required
					label="Confirm Password"
					name="confirmPassword"
					disabled={isLoading}
				/>
			</div>
			{displayError && (
				<p className="text-red-500 text-sm">{displayError}</p>
			)}
			<BubbleButton type="submit" disabled={isLoading}>
				{isLoading ? "Setting up..." : "Continue"}
			</BubbleButton>
		</form>
	);
}
