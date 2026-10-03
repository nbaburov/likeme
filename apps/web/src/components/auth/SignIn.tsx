/* eslint-disable @next/next/no-img-element */
/* eslint-disable react/no-unescaped-entities */
import React, { ReactNode } from "react";
import { FiArrowLeft } from "react-icons/fi";
import { motion } from "framer-motion";
import { twMerge } from "tailwind-merge";
import { Input } from "@components/ui/Input";
import { BubbleButton } from "@components/ui/BubbleButton";

interface SignInFormProps {
	username: string;
	password: string;
	isLoading: boolean;
	onUsernameChange: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	onPasswordChange: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	onSubmit: (e: React.FormEvent) => void;
	onBack: () => void;
}

export const SignInGrid = ({
	username,
	password,
	isLoading,
	onUsernameChange,
	onPasswordChange,
	onSubmit,
	onBack,
}: SignInFormProps) => {
	return (
		<div className="bg-likeme-third py-20 text-zinc-200 selection:bg-zinc-600 w-full h-full min-h-screen">
			<BubbleButton
				className="absolute left-4 top-6 text-sm"
				onClick={onBack}
			>
				<FiArrowLeft />
				Go back
			</BubbleButton>

			<motion.div
				initial={{
					opacity: 0,
					y: 25,
				}}
				animate={{
					opacity: 1,
					y: 0,
				}}
				transition={{
					duration: 1.25,
					ease: "easeInOut",
				}}
				className="flex flex-col my-auto relative z-10 mx-auto w-full max-w-xl p-4"
			>
				<Heading />

				<EmailForm
					username={username}
					password={password}
					isLoading={isLoading}
					onUsernameChange={onUsernameChange}
					onPasswordChange={onPasswordChange}
					onSubmit={onSubmit}
				/>
				<Terms />
			</motion.div>
		</div>
	);
};

const Heading = () => (
	<div>
		<NavLogo />
		<div className="mb-9 mt-6 space-y-1.5">
			<h1 className="text-2xl text-black font-semibold">
				Sign in to your account
			</h1>
			<p className="text-zinc-400">
				Don't have an account?{" "}
				<a href="/sign-up" className="text-orange-400">
					Create one.
				</a>
			</p>
		</div>
	</div>
);

interface EmailFormProps {
	username: string;
	password: string;
	isLoading: boolean;
	onUsernameChange: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	onPasswordChange: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	onSubmit: (e: React.FormEvent) => void;
}

const EmailForm = ({
	username,
	password,
	isLoading,
	onUsernameChange,
	onPasswordChange,
	onSubmit,
}: EmailFormProps) => {
	return (
		<form onSubmit={onSubmit}>
			<div className="mb-3">
				<Input
					label="Username"
					placeholder="john.doe"
					name="username"
					type="text"
					value={username}
					onChange={onUsernameChange}
				/>
			</div>
			<div className="mb-6">
				<Input
					label="Password"
					placeholder="••••••••••••"
					name="password"
					type="password"
					value={password}
					onChange={onPasswordChange}
				/>
			</div>
			<SplashButton type="submit" className="w-full" disabled={isLoading}>
				{isLoading ? "Signing in..." : "Sign in"}
			</SplashButton>
		</form>
	);
};

const Terms = () => (
	<p className="mt-9 text-xs text-zinc-400">
		By signing in, you agree to our{" "}
		<a href="/terms" className="text-orange-400">
			Terms & Conditions
		</a>{" "}
		and{" "}
		<a href="/privacy" className="text-orange-400">
			Privacy Policy.
		</a>
	</p>
);

const SplashButton = ({ children, className, ...rest }: ButtonProps) => {
	return (
		<button
			className={twMerge(
				"rounded-md bg-gradient-to-br from-likeme-primary to-likeme-accent px-4 py-2 text-lg text-orange-50 ring-2 ring-orange-500/50 ring-offset-2 ring-orange-950 transition-all hover:scale-[1.02] hover:ring-transparent active:scale-[0.98] active:ring-orange-500/70",
				className
			)}
			{...rest}
		>
			{children}
		</button>
	);
};

const NavLogo = () => {
	return <img src="/logo.png" alt="Likeme" className="w-10" />;
};

type ButtonProps = {
	children: ReactNode;
	className?: string;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;
