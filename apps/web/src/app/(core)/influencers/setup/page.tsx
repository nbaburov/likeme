"use client";

import { Suspense, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { usePublicInfluencer } from "@/hooks/useInfluencer";
import { SetupForm } from "@components/influencer/client/SetupForm";
import InfoPageLayout from "@components/layout/client/InfoPageLayout";
import { Alert } from "@material-tailwind/react";

function SetupContent() {
	const router = useRouter();
	const searchParams = useSearchParams();
	const token = searchParams.get("token");
	const { setup: completeSetup, isSettingUp } = usePublicInfluencer();

	useEffect(() => {
		if (!token) {
			router.push("/");
		}
	}, [token, router]);

	const handleSetup = async (password: string, confirmPassword: string) => {
		if (password !== confirmPassword) {
			return;
		}

		await completeSetup({ password, token: token! });
		router.push("/influencer/settings/instagram");
	};

	if (!token) {
		return (
			<Alert color="warning" className="mt-4">
				Invalid setup link. Please request a new one.
			</Alert>
		);
	}

	return (
		<div className="flex flex-col items-center justify-center w-full">
			<SetupForm onSubmit={handleSetup} isLoading={isSettingUp} />
		</div>
	);
}

export default function InfluencerSetup() {
	return (
		<InfoPageLayout title="Complete Your Account Setup">
			<Suspense fallback={<div>Loading...</div>}>
				<SetupContent />
			</Suspense>
		</InfoPageLayout>
	);
}
