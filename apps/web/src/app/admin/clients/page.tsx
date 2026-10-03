"use client";
import { useState } from "react";
import { Modal } from "@components/ui/Modal";
import { useClient } from "@/hooks/useClient";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";
import { AddClientForm } from "@components/admin/client/AddClientForm";
import { CreateClientRequest } from "@/dto/ClientDTO";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import BubbleButton from "@components/ui/BubbleButton";
import { IoMdAdd } from "react-icons/io";
import { usePublicClient } from "@hooks/useClient";

export default function ClientsPage() {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const { data: clients, isLoading } = useClient();
	const { create, isCreating } = usePublicClient();
	const { upload, isUploading } = usePublicFileUpload();
	const router = useRouter();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "clients" });
		return result.fileName;
	};

	const handleCreateClient = async (data: CreateClientRequest) => {
		await create(data);
		setIsModalOpen(false);
	};

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}


	const formattedClients = clients.map((client) => ({
		id: client.id,
		username: client.username,
		email: client.email,
		instagram: client.instagramHandle,
		"Instagram Connected": client.isInstagramConnected ? "Yes" : "No",
		"Active": client.isActive ? "Yes" : "No",
		country: client.billingDetails.country,
	}));

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">Clients</h1>
				<BubbleButton onClick={() => setIsModalOpen(true)}>
					<IoMdAdd />
					Add Client
				</BubbleButton>
			</div>

			<LikeMeTable
				data={formattedClients}
				onRowClick={(id) => router.push(`/admin/clients/${id}`)}
				filterableColumns={["country", "Active", "Instagram Connected"]}
			/>

			<Modal
				isOpen={isModalOpen}
				setIsOpen={setIsModalOpen}
				title="Add New Client"
			>
				<AddClientForm
					onSubmit={handleCreateClient}
					onClose={() => setIsModalOpen(false)}
					isLoading={isCreating}
					handleUpload={handleUpload}
					isUploading={isUploading}
				/>
			</Modal>
		</div>
	);
}
