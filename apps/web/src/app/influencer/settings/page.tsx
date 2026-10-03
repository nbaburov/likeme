"use client";
import { useState } from "react";
import { useInfluencerById, useInfluencer } from "@/hooks/useInfluencer";
import { useAuth } from "@/hooks/useAuth";
import { InfluencerSettingsInfo } from "@components/influencer/admin/InfluencerSettingsInfo";
import { EditInfluencerSettingsForm } from "@components/influencer/admin/EditInfluencerSettingsForm";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { Modal } from "@components/ui/Modal";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { Button } from "@material-tailwind/react";
import { UpdateInfluencerRequest } from "@dto/InfluencerDTO";

export default function InfluencerSettingsPage() {
	const { getUserId, logout } = useAuth();
	const userId = getUserId();

	// Queries
	const { data: influencer, isLoading } = useInfluencerById(userId || 0);

	// Mutations
	const {
		update,
		delete: deleteInfluencer,
		isUpdating,
		isDeleting,
	} = useInfluencer();

	// File Upload
	const { upload, isUploading } = usePublicFileUpload();

	const [isEditing, setIsEditing] = useState(false);
	const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

	if (isLoading || !influencer) {
		return <CustomSkeleton count={3} />;
	}

	const handleUpdate = async (data: UpdateInfluencerRequest) => {
		// Create a copy of the data
		const updateData = { ...data };

		// Remove password if empty
		if (!updateData.password) {
			delete updateData.password;
		}
		
		await update({ id: userId || 0, influencer: updateData });
		setIsEditing(false);
	};

	const handleDelete = async () => {
		await deleteInfluencer(userId || 0);
		logout();
	};

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "influencer-photos" });
		return result.fileName; 
	};

	return (
		<div className="container mx-auto px-4 py-8">
			{isEditing ? (
				<EditInfluencerSettingsForm
					influencer={influencer}
					onSubmit={handleUpdate}
					onCancel={() => setIsEditing(false)}
					isLoading={isUpdating}
					handleUpload={handleUpload}
					isUploading={isUploading}
				/>
			) : (
				<InfluencerSettingsInfo
					influencer={influencer}
					onEdit={() => setIsEditing(true)}
					onLogout={logout}
					onDeleteAccount={() => setIsDeleteModalOpen(true)}
					isDeleting={isDeleting}
				/>
			)}

			<Modal
				isOpen={isDeleteModalOpen}
				setIsOpen={setIsDeleteModalOpen}
				title="Delete Account"
			>
				<div className="p-6">
					<p className="mb-4">
						Are you sure you want to delete your account? This
						action cannot be undone.
					</p>
					<div className="flex justify-end gap-4">
						<Button
							color="secondary"
							onClick={() => setIsDeleteModalOpen(false)}
						>
							Cancel
						</Button>
						<Button
							color="error"
							onClick={handleDelete}
							disabled={isDeleting}
						>
							{isDeleting ? "Deleting..." : "Delete Account"}
						</Button>
					</div>
				</div>
			</Modal>
		</div>
	);
}
