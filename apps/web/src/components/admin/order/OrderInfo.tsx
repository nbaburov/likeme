import { Button } from "@material-tailwind/react";
import { OrderResponse } from "@/dto/OrderDTO";
import { IoIosArrowBack } from "react-icons/io";

interface OrderInfoProps {
	order: OrderResponse;
	onBack: () => void;
	onNavigateToOffer: (offerId: number) => void;
	onNavigateToInvoice: (invoiceId: number) => void;
}

export const OrderInfo = ({
	order,
	onBack,
	onNavigateToOffer,
	onNavigateToInvoice,
}: OrderInfoProps) => {
	return (
		<div>
			<div className="px-4 sm:px-0 flex justify-between items-center">
				<div className="flex items-center">
					<Button color="secondary" onClick={onBack} className="mr-2">
						<IoIosArrowBack className="mr-1" />
						Back
					</Button>
					<h3 className="text-base font-semibold leading-7 text-gray-900">
						Order Information
					</h3>
				</div>
			</div>

			<div className="mt-6 border-t border-gray-100">
				<dl className="divide-y divide-gray-100">
					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Order ID
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{order.id}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Status
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{order.status}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Offer Details
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							<div className="flex justify-between items-center">
								<div>
									<p>Title: {order.offer.title}</p>
									<p>Price: ${order.offer.price}</p>
								</div>
								<Button 
									color="secondary"
									size="sm"
									onClick={() => onNavigateToOffer(order.offer.id)}
								>
									View Offer
								</Button>
							</div>
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Order Details
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							<p>Post ID: {order.details.postId}</p>
							<p>Comment: {order.details.comment}</p>
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Invoice
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							<div className="flex justify-between items-center">
								<p>Status: {order.invoice.status}</p>
								<Button 
									color="secondary"
									size="sm"
									onClick={() => onNavigateToInvoice(order.invoice.id)}
								>
									View Invoice
								</Button>
							</div>
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Created On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(order.createdOn).toLocaleDateString()}
						</dd>
					</div>

					<div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
						<dt className="text-sm font-medium leading-6 text-gray-900">
							Updated On
						</dt>
						<dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">
							{new Date(order.updatedOn).toLocaleDateString()}
						</dd>
					</div>
				</dl>
			</div>
		</div>
	);
};
