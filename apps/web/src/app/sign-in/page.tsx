/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState } from "react";
import { SignInGrid } from "@components/auth/SignIn";
import { useAuth } from "@/hooks/useAuth";
import type { AuthRequest } from "@/dto/AuthDTO";
import { useRouter } from "next/navigation";
import BarLoader from "@components/ui/BarLoader";

export default function SignInPage() {
	const { login, isLoading } = useAuth();
	const router = useRouter();
	const [formData, setFormData] = useState<AuthRequest>({
		username: "",
		password: "",
	});

	const handleInputChange = (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => {
		const { name, value } = e.target;
		setFormData((prev) => ({
			...prev,
			[name]: value,
		}));
	};

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		// Remove tokens before login attempt
		localStorage.removeItem("likeme_access_token");
		localStorage.removeItem("likeme_refresh_token");
		login(formData);
	};

	if (isLoading) {
		return <BarLoader />;
	}

	return (
		<div className="mt-16">
			<SignInGrid
				username={formData.username}
				password={formData.password}
				isLoading={isLoading}
				onUsernameChange={handleInputChange}
				onPasswordChange={handleInputChange}
				onSubmit={handleSubmit}
				onBack={() => router.push("/")}
			/>
		</div>
	);
}
