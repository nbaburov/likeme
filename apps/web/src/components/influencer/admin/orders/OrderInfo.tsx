// src/components/influencer/order/OrderInfo.tsx
import { OrderResponse, OrderStatus } from "@/dto/OrderDTO";
import { Card, CardBody, Typography, Button } from "@material-tailwind/react";
import { BubbleButton } from "@components/ui/BubbleButton";
import { IoArrowBack } from "react-icons/io5";
import { InvoiceStatus } from "@/dto/InvoiceDTO";

interface OrderInfoProps {
    order: OrderResponse;
    onComplete: () => void;
    onBack: () => void;
    onNavigateToOffer: (offerId: number) => void;
    onNavigateToInvoice: (invoiceId: number) => void;
    isCompleting: boolean;
}

export const OrderInfo = ({
    order,
    onComplete,
    onBack,
    onNavigateToOffer,
    onNavigateToInvoice,
    isCompleting,
}: OrderInfoProps) => {
    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <BubbleButton onClick={onBack}>
                    <IoArrowBack />
                    Back
                </BubbleButton>
                <h1 className="text-2xl font-semibold">Order Details</h1>
            </div>

            <Card className="bg-white">
                <CardBody className="space-y-6">
                    <div className="flex justify-between items-start">
                        <div>
                            <Typography variant="h5" color="inherit">
                                {order.offer.title}
                            </Typography>
                            <Typography color="inherit" className="mt-1 text-gray-600">
                                Order ID: {order.orderedById}
                            </Typography>
                        </div>
                        <Button
                            size="sm"
                            color={order.status === OrderStatus.COMPLETE ? "success" : "info"}
                        >
                            {order.status}
                        </Button>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Typography className="font-semibold">Price</Typography>
                            <Typography>${order.offer.price}</Typography>
                        </div>
                        <div>
                            <Typography className="font-semibold">Created On</Typography>
                            <Typography>
                                {new Date(order.createdOn).toLocaleDateString()}
                            </Typography>
                        </div>
                    </div>

                    {order.details.comment && (
                        <div>
                            <Typography className="font-semibold">Comment</Typography>
                            <Typography>{order.details.comment}</Typography>
                        </div>
                    )}

                    <div className="flex gap-4">
                        <Button
                            onClick={() => onNavigateToOffer(order.offer.id)}
                            variant="outline"
                        >
                            View Offer
                        </Button>
                        {order.invoice && (
                            <Button
                                onClick={() => onNavigateToInvoice(order.invoice.id)}
                                variant="outline"
                            >
                                View Invoice
                            </Button>
                        )}
                        {order.invoice?.status === InvoiceStatus.PAID && 
                         order.status !== OrderStatus.COMPLETE && (
                            <Button
                                onClick={onComplete}
                                disabled={isCompleting}
                                color="success"
                            >
                                {isCompleting ? "Completing..." : "Complete Order"}
                            </Button>
                        )}
                    </div>
                </CardBody>
            </Card>
        </div>
    );
};