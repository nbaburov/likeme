"use client";

import { useState } from "react";
import { useClientById, useClient } from "@/hooks/useClient";
import { useAuth } from "@/hooks/useAuth";
import { ClientInfo } from "@components/client/admin/settings/account/ClientInfo";
import { EditClientForm } from "@components/client/admin/settings/account/EditClientForm";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateClientRequest } from "@/dto/ClientDTO";
import { useRouter } from "next/navigation";

export default function AccountSettingsPage() {
	const router = useRouter();
	const { getUserId, logout } = useAuth();
	const userId = getUserId();
	
	// Queries and Mutations
	const { data: client, isLoading: isLoadingClient } = useClientById(userId ?? 0);
	const { update, delete: deleteClient, isUpdating, isDeleting } = useClient();
	const [isEditing, setIsEditing] = useState(false);
	const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

	// Loading states
	if (isLoadingClient || isDeleting) {
		return (
			<div className="container mx-auto px-4 py-8">
				<CustomSkeleton className="h-8 w-48 mb-6" />
				<div className="space-y-4">
					<CustomSkeleton count={6} className="h-12" />
				</div>
			</div>
		);
	}

	if (!client) {
		router.push("/sign-in");
		return null;
	}

	const handleUpdate = async (data: UpdateClientRequest) => {
		if (!userId) return;
		
		await update({
			id: userId,
			client: data
		});
		setIsEditing(false);
	};

	const handleDeleteAccount = async () => {
		if (!userId) return;
		
		await deleteClient(userId);
		setIsDeleteModalOpen(false);
		logout();
		router.push("/sign-in");
	};

	return (
		<div className="container mx-auto px-4 py-8">
			<h1 className="text-2xl font-bold mb-6">Account Settings</h1>
			{isEditing ? (
				<EditClientForm
					client={client}
					onSubmit={handleUpdate}
					onCancel={() => setIsEditing(false)}
					isLoading={isUpdating}
				/>
			) : (
				<ClientInfo
					client={client}
					onEdit={() => setIsEditing(true)}
					onLogout={logout}
					onDeleteAccount={() => setIsDeleteModalOpen(true)}
				/>
			)}

			{isDeleteModalOpen && (
				<div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
					<div className="bg-white rounded-lg p-6 max-w-md w-full">
						<h2 className="text-xl font-bold mb-4">Confirm Account Deletion</h2>
						<p className="text-gray-600 mb-6">
							Are you sure you want to delete your account? This action cannot be undone.
						</p>
						<div className="flex justify-end space-x-4">
							<button
								className="px-4 py-2 bg-gray-200 rounded-lg"
								onClick={() => setIsDeleteModalOpen(false)}
								disabled={isDeleting}
							>
								Cancel
							</button>
							<button
								className="px-4 py-2 bg-red-500 text-white rounded-lg"
								onClick={handleDeleteAccount}
								disabled={isDeleting}
							>
								{isDeleting ? "Deleting..." : "Delete Account"}
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}
