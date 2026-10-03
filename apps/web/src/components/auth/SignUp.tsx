import React, { ReactNode, ChangeEvent } from "react";
import { FiArrowLeft } from "react-icons/fi";
import { motion } from "framer-motion";
import { twMerge } from "tailwind-merge";
import { Input } from "@components/ui/Input";
import { BubbleButton } from "@components/ui/BubbleButton";
import { FileUpload } from "@components/ui/FileUpload";
import { Select } from "@components/ui/Select";
import type { CreateClientRequest } from "@/dto/ClientDTO";
import { countries } from "@/utils/countries";
import { usePublicFileUpload } from "@/hooks/useFileUpload";

interface SignUpFormProps {
	formData: CreateClientRequest;
	setFormData: (data: CreateClientRequest) => void;
	isLoading: boolean;
	onSubmit: (data: CreateClientRequest) => Promise<void>;
	onClose: () => void;
}

export const SignUpGrid = ({
	formData,
	setFormData,
	isLoading,
	onSubmit,
	onClose,
}: SignUpFormProps) => {
	const { upload, isUploading } = usePublicFileUpload();

	const handleInputChange = (
		e: ChangeEvent<
			HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
		>
	) => {
		const { name, value } = e.target;
		if (name.includes("billingDetails.")) {
			const field = name.split(".")[1];
			setFormData({
				...formData,
				billingDetails: {
					...formData.billingDetails,
					[field]: value,
				},
			});
		} else {
			setFormData({ ...formData, [name]: value });
		}
	};

	const handleFileUpload = async (file: File) => {
		const result = await upload({ file, prefix: "clients" });
		if (result.fileName) {
			setFormData({
				...formData,
				profilePhotoPath: result.fileName,
			});
		}
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();

		await onSubmit(formData);
	};

	return (
		<div className="bg-likeme-third py-20 text-zinc-200 selection:bg-zinc-600 w-full h-full min-h-screen">
			<BubbleButton
				className="absolute left-4 top-6 text-sm"
				onClick={onClose}
			>
				<FiArrowLeft />
				Go back
			</BubbleButton>

			<motion.div
				initial={{ opacity: 0, y: 25 }}
				animate={{ opacity: 1, y: 0 }}
				transition={{ duration: 1.25, ease: "easeInOut" }}
				className="flex flex-col my-auto relative z-10 mx-auto w-full max-w-xl p-4"
			>
				<Heading />
				<form onSubmit={handleSubmit} className="space-y-4">
					<Input
						label="Username"
						name="username"
						value={formData.username}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Email"
						name="email"
						type="email"
						value={formData.email}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Password"
						name="password"
						type="password"
						value={formData.password}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<Input
						label="Instagram Handle"
						name="instagramHandle"
						type="text"
						value={formData.instagramHandle}
						onChange={handleInputChange}
						required
						disabled={isLoading}
					/>
					<FileUpload
						label="Profile Photo"
						onUpload={handleFileUpload}
						disabled={isLoading || isUploading}
						required
					/>
					<div className="space-y-4">
						<h3 className="text-lg font-semibold text-black">
							Billing Details
						</h3>
						<div className="grid grid-cols-2 gap-4">
							<Input
								label="First Name"
								name="billingDetails.firstName"
								type="text"
								value={formData.billingDetails.firstName}
								onChange={handleInputChange}
								required
								disabled={isLoading}
							/>
							<Input
								label="Last Name"
								name="billingDetails.lastName"
								type="text"
								value={formData.billingDetails.lastName}
								onChange={handleInputChange}
								required
								disabled={isLoading}
							/>
						</div>
						<Input
							label="Street Address"
							name="billingDetails.streetAddress"
							type="text"
							value={formData.billingDetails.streetAddress}
							onChange={handleInputChange}
							required
							disabled={isLoading}
						/>
						<div className="grid grid-cols-2 gap-4">
							<Input
								label="City"
								name="billingDetails.city"
								type="text"
								value={formData.billingDetails.city}
								onChange={handleInputChange}
								required
								disabled={isLoading}
							/>
							<Input
								label="State/Province"
								name="billingDetails.state"
								type="text"
								value={formData.billingDetails.state}
								onChange={handleInputChange}
								required
								disabled={isLoading}
							/>
						</div>
						<div className="grid grid-cols-2 gap-4">
							<Select
								label="Country"
								name="billingDetails.country"
								value={formData.billingDetails.country}
								onChange={handleInputChange}
								options={countries}
								required
								disabled={isLoading}
							/>
							<Input
								label="ZIP/Postal Code"
								name="billingDetails.zipCode"
								type="text"
								value={formData.billingDetails.zipCode}
								onChange={handleInputChange}
								required
								disabled={isLoading}
							/>
						</div>
					</div>
					<SplashButton
						type="submit"
						className="w-full"
						disabled={isLoading}
					>
						{isLoading ? (
							<span className="flex items-center justify-center gap-2">
								<span className="animate-spin">⌛</span>
								{isLoading ? "Processing..." : "Create account"}
							</span>
						) : (
							"Create account"
						)}
					</SplashButton>
				</form>
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
				Create your account
			</h1>
			<p className="text-zinc-400">
				Already have an account?{" "}
				<a href="/sign-in" className="text-orange-400">
					Sign in.
				</a>
			</p>
		</div>
	</div>
);

const Terms = () => (
	<p className="mt-9 text-xs text-zinc-400">
		By creating an account, you agree to our{" "}
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
