import { Button } from "@material-tailwind/react";
import { InvoiceResponse, UpdateInvoiceRequest } from "@/dto/InvoiceDTO";
import { useState } from "react";
import { EditInvoiceForm } from "./EditInvoiceForm";
import { IoIosArrowBack } from "react-icons/io";

interface InvoiceInfoProps {
	invoice: InvoiceResponse;
	onDelete: () => Promise<void>;
	onUpdate: (data: UpdateInvoiceRequest) => Promise<void>;
	onBack: () => void;
	isUpdating: boolean;
	isDeleting: boolean;
}

export const InvoiceInfo = ({
	invoice,
	onDelete,
	onUpdate,
	onBack,
	isUpdating,
	isDeleting,
}: InvoiceInfoProps) => {
	const [isEditing, setIsEditing] = useState(false);

	if (isEditing) {
		return (
			<div className="max-w-2xl mx-auto">
				<div className="flex justify-between items-center mb-6">
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Edit Invoice
					</h3>
					<Button
						color="secondary"
						onClick={() => setIsEditing(false)}
						disabled={isUpdating || isDeleting}
					>
						Cancel
					</Button>
				</div>
				<EditInvoiceForm
					invoice={invoice}
					onSubmit={async (data) => {
						await onUpdate(data);
						setIsEditing(false);
					}}
					onClose={() => setIsEditing(false)}
					isLoading={isUpdating || isDeleting}
				/>
			</div>
		);
	}

	return (
		<div>
			<div className="px-4 sm:px-0 flex justify-between items-center">
				<div className="flex items-center">
					<Button color="secondary" onClick={onBack} className="mr-2">
						<IoIosArrowBack className="mr-1" />
						Back
					</Button>
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Invoice Information
					</h3>
				</div>
				<div className="flex gap-2">
					<Button
						color="secondary"
						onClick={() => setIsEditing(true)}
						disabled={isUpdating}
					>
						Edit
					</Button>
					<Button
						color="error"
						onClick={onDelete}
						disabled={isDeleting}
					>
						Delete
					</Button>
				</div>
			</div>

			<div className="mt-6 border-t border-gray-100">
				<dl className="divide-y divide-gray-100">
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Invoice ID
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{invoice.id}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Amount
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							${invoice.amount}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{invoice.status}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Updated On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(invoice.updatedOn).toLocaleDateString()}
						</dd>
					</div>
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Created On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(invoice.createdOn).toLocaleDateString()}
						</dd>
					</div>
				</dl>
			</div>
		</div>
	);
};
