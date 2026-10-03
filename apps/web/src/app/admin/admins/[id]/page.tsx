"use client";
import { useParams, useRouter } from "next/navigation";
import { useAdminById, useAdmin } from "@/hooks/useAdmin";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { AdminInfo } from "@components/admin/admin/AdminInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateAdminRequest } from "@/dto/AdminDTO";

export default function AdminDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: admin, isLoading } = useAdminById(Number(id));
	const { update, delete: deleteAdmin, isUpdating, isDeleting } = useAdmin();
	const { upload, isUploading } = usePublicFileUpload();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "admins" });
		return result.fileName;
	};

	const handleDelete = async () => {
		await deleteAdmin(Number(id));
		router.push("/admin/admins");
	};

	const handleUpdate = async (data: UpdateAdminRequest) => {
		await update({ id: Number(id), admin: data });
	};

	if (isLoading || !admin) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<AdminInfo
				admin={admin}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin/admins")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>
		</div>
	);
}
