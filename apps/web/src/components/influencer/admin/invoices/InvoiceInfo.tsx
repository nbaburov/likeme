// src/components/influencer/invoices/InvoiceInfo.tsx
import { InvoiceResponse } from "@/dto/InvoiceDTO";
import { Card, CardBody, Typography } from "@material-tailwind/react";
import { BubbleButton } from "@components/ui/BubbleButton";
import { IoArrowBack } from "react-icons/io5";

interface InvoiceInfoProps {
	invoice: InvoiceResponse;
	onBack: () => void;
}

export const InvoiceInfo = ({ invoice, onBack }: InvoiceInfoProps) => {
	return (
		<div className="space-y-6">
			<div className="flex items-center gap-4">
				<BubbleButton onClick={onBack}>
					<IoArrowBack />
					Back
				</BubbleButton>
				<h1 className="text-2xl font-semibold">Invoice Details</h1>
			</div>

			<Card>
				<CardBody className="space-y-6">
					<div className="flex justify-between items-start">
						<div>
							<Typography variant="h5" color="inherit">
								Invoice #{invoice.id}
							</Typography>
							<Typography
								color="inherit"
								className="mt-1 text-gray-600"
							>
								Status: {invoice.status}
							</Typography>
						</div>
					</div>

					<div className="grid grid-cols-2 gap-6">
						<div>
							<Typography className="font-semibold">
								Amount
							</Typography>
							<Typography>${invoice.amount}</Typography>
						</div>
						<div>
							<Typography className="font-semibold">
								Created On
							</Typography>
							<Typography>
								{new Date(
									invoice.createdOn
								).toLocaleDateString()}
							</Typography>
						</div>
						<div>
							<Typography className="font-semibold">
								Last Updated
							</Typography>
							<Typography>
								{new Date(
									invoice.updatedOn
								).toLocaleDateString()}
							</Typography>
						</div>
					</div>
				</CardBody>
			</Card>
		</div>
	);
};
