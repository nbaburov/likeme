/* eslint-disable @typescript-eslint/no-unused-vars */
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { useInfluencer, useInfluencerById } from "@/hooks/useInfluencer";
import { InstagramConnectForm } from "@components/influencer/admin/InstagramConnectForm";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function InfluencerInstagramSettingsPage() {
	const router = useRouter();
	const { isAuthenticated, getUserId, getUserRole } = useAuth();
	const userId = getUserId();
	const { connectInstagram, isConnectingInstagram, update } = useInfluencer();
	const {
		data: influencer,
		isLoading,
		error: fetchError,
	} = useInfluencerById(userId || 0);
	const [accessToken, setAccessToken] = useState("");

	useEffect(() => {
		const checkAuth = async () => {
			const authenticated = await isAuthenticated();
			if (!authenticated || getUserRole() !== "INFLUENCER") {
				router.push("/sign-in");
			}
		};
		checkAuth();
	}, [isAuthenticated, getUserRole, router]);

	if (isLoading) return <CustomSkeleton />;
	if (fetchError) return null; // Error will be shown via toast

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		if (!userId) return;

		await connectInstagram({
			id: userId,
			request: { instagramAccessToken: accessToken },
		});
		setAccessToken("");
	};

	const handleRemoveAccess = async () => {
		if (!userId) return;

		await update({
			id: userId,
			influencer: {
				isInstagramConnected: false,
				instagramAccessToken: "",
				status: "PENDING_INSTAGRAM",
			},
		});
	};

	return (
		<InstagramConnectForm
			user={influencer}
			accessToken={accessToken}
			setAccessToken={setAccessToken}
			isConnectingInstagram={isConnectingInstagram}
			onSubmit={handleSubmit}
			onRemoveAccess={handleRemoveAccess}
		/>
	);
}
