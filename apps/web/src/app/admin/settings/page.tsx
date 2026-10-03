"use client";
import { useAuth } from "@/hooks/useAuth";
import { useAdmin, useAdminById } from "@/hooks/useAdmin";
import { AdminInfo } from "@components/admin/admin/AdminInfo";
import { UpdateAdminRequest } from "@/dto/AdminDTO";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { Modal } from "@components/ui/Modal";
import { Alert } from "@material-tailwind/react";
import BubbleButton from "@/components/ui/BubbleButton";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { useEffect, useState } from "react";

export default function AdminSettingsPage() {
	const { getUserId, logout } = useAuth();
	const userId = getUserId();
	const router = useRouter();

	const { data: adminData, isLoading } = useAdminById(userId ?? 0);
	const { update, delete: deleteAdmin, isUpdating, isDeleting } = useAdmin();
	const { upload, isUploading } = usePublicFileUpload();
	const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

	useEffect(() => {
		if (!userId) {
			router.push("/sign-in");
		}
	}, [userId, router]);

	const handleUpdate = async (data: UpdateAdminRequest) => {
		if (!userId) return;
		await update({ id: userId, admin: data });
	};

	const handleDelete = async () => {
		if (!userId) return;
		await deleteAdmin(userId);
		router.push("/sign-in");
	};

	const handleUpload = async (file: File): Promise<string> => {
		const response = await upload({
			file,
			prefix: "admin-profiles",
		});
		return response.fileName;
	};

	if (isLoading) {
		return (
			<div className="space-y-4">
				<CustomSkeleton count={6} className="h-12" />
			</div>
		);
	}
	if (!adminData) {
		return (
			<Alert color="error" className="mb-4">
				Failed to load admin data
			</Alert>
		);
	}

	return (
		<div>
			<div className="flex justify-between items-center mb-6">
				<h2 className="text-xl font-semibold">Account Settings</h2>
				<BubbleButton onClick={logout}>Logout</BubbleButton>
			</div>

			<AdminInfo
				admin={adminData}
				onDelete={async () => setIsDeleteModalOpen(true)}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>

			<Modal
				isOpen={isDeleteModalOpen}
				setIsOpen={setIsDeleteModalOpen}
				title="Confirm Account Deletion"
			>
				<p>
					Are you sure you want to delete your account? This action
					cannot be undone.
				</p>
				<div className="mt-4 flex gap-4">
					<button
						className="bg-red-500 text-white px-4 py-2 rounded"
						onClick={handleDelete}
						disabled={isDeleting}
					>
						{isDeleting ? "Deleting..." : "Confirm"}
					</button>
					<button
						className="bg-gray-500 text-white px-4 py-2 rounded"
						onClick={() => setIsDeleteModalOpen(false)}
						disabled={isDeleting}
					>
						Cancel
					</button>
				</div>
			</Modal>
		</div>
	);
}
