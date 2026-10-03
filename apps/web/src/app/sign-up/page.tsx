"use client";

import { SignUpGrid } from "@components/auth/SignUp";
import type { CreateClientRequest } from "@/dto/ClientDTO";
import { usePublicClient } from "@/hooks/useClient";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function SignUpPage() {
	const router = useRouter();
	const { create, isCreating } = usePublicClient();
	const [formData, setFormData] = useState<CreateClientRequest>({
		username: "",
		email: "",
		password: "",
		profilePhotoPath: "",
		instagramHandle: "",
		billingDetails: {
			firstName: "",
			lastName: "",
			country: "United States",
			streetAddress: "",
			city: "",
			state: "",
			zipCode: "",
		},
	});

	const handleCreateClient = async (data: CreateClientRequest) => {
		await create(data);
		router.push("/sign-in");
	};

	if (isCreating) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="mt-16">
			<SignUpGrid
				formData={formData}
				setFormData={setFormData}
				isLoading={isCreating}
				onSubmit={handleCreateClient}
				onClose={() => router.push("/")}
			/>
		</div>
	);
}
