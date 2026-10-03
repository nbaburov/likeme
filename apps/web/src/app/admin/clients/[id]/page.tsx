"use client";
import { useParams, useRouter } from "next/navigation";
import { useClientById, useClient } from "@/hooks/useClient";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { ClientInfo } from "@components/admin/client/ClientInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateClientRequest } from "@/dto/ClientDTO";

export default function ClientDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: client, isLoading } = useClientById(Number(id));
	const {
		update,
		delete: deleteClient,
		isUpdating,
		isDeleting,
	} = useClient();
	const { upload, isUploading } = usePublicFileUpload();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "clients" });
		return result.fileName;
	};

	const handleDelete = async () => {
		await deleteClient(Number(id));
		router.push("/admin/clients");
	};

	const handleUpdate = async (data: UpdateClientRequest) => {
		await update({ id: Number(id), client: data });
	};

	if (isLoading || !client) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<ClientInfo
				client={client}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin/clients")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>
		</div>
	);
}
