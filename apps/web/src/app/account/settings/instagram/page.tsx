/* eslint-disable @typescript-eslint/no-unused-vars */
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { useClient, useClientById } from "@/hooks/useClient";
import { InstagramConnectForm } from "@components/influencer/admin/InstagramConnectForm";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function ClientInstagramSettingsPage() {
	const router = useRouter();
	const { isAuthenticated, getUserId, getUserRole } = useAuth();
	const userId = getUserId();
	const { update: updateClient, isUpdating } = useClient();
	const { data: client, isLoading, error: fetchError } = useClientById(userId || 0);
	const [accessToken, setAccessToken] = useState("");

	useEffect(() => {
		const checkAuth = async () => {
			const authenticated = await isAuthenticated();
			if (!authenticated || getUserRole() !== "CLIENT") {
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

		await updateClient({
			id: userId,
			client: {
				isInstagramConnected: true,
				instagramAccessToken: accessToken,
			},
		});
		setAccessToken("");
	};

	const handleRemoveAccess = async () => {
		if (!userId) return;

		await updateClient({
			id: userId,
			client: {
				isInstagramConnected: false,
				instagramAccessToken: "",
			},
		});
	};

	return (
		<InstagramConnectForm
			user={client}
			accessToken={accessToken}
			setAccessToken={setAccessToken}
			isConnectingInstagram={isUpdating}
			onSubmit={handleSubmit}
			onRemoveAccess={handleRemoveAccess}
		/>
	);
}
