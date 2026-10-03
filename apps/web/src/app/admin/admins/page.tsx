"use client";
import { useState } from "react";
import { Modal } from "@components/ui/Modal";
import { useAdmin } from "@/hooks/useAdmin";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";
import { AddAdminForm } from "@components/admin/admin/AddAdminForm";
import { CreateAdminRequest } from "@/dto/AdminDTO";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import BubbleButton from "@components/ui/BubbleButton";
import { IoMdAdd } from "react-icons/io";

export default function AdminsPage() {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const { data: admins, isLoading, create, isCreating } = useAdmin();
	const { upload, isUploading } = usePublicFileUpload();
	const router = useRouter();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "admins" });
		return result.fileName;
	};

	const handleCreateAdmin = async (data: CreateAdminRequest) => {
		await create(data);
		setIsModalOpen(false);
	};

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedAdmins = admins.map((admin) => ({
		id: admin.id,
		username: admin.username,
		email: admin.email,
		permissions: admin.permissions,
		status: admin.isActive ? "Active" : "Inactive",
	}));

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">Admins</h1>
				<BubbleButton onClick={() => setIsModalOpen(true)}>
					<IoMdAdd />
					Add Admin
				</BubbleButton>
			</div>

			<LikeMeTable
				data={formattedAdmins}
				onRowClick={(id) => router.push(`/admin/admins/${id}`)}
			/>

			<Modal
				isOpen={isModalOpen}
				setIsOpen={setIsModalOpen}
				title="Add New Admin"
			>
				<AddAdminForm
					onSubmit={handleCreateAdmin}
					onClose={() => setIsModalOpen(false)}
					isLoading={isCreating}
					handleUpload={handleUpload}
					isUploading={isUploading}
				/>
			</Modal>
		</div>
	);
}
