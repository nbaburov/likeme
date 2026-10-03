import { Button } from "@material-tailwind/react";
import { InvoiceResponse } from "@/dto/InvoiceDTO";

interface InvoiceInfoProps {
  invoice: InvoiceResponse;
  onPay: () => Promise<void>;
  onBack: () => void;
  isPaying: boolean;
}

export function InvoiceInfo({ invoice, onPay, onBack, isPaying }: InvoiceInfoProps) {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-semibold">Invoice Details</h2>
        <Button onClick={onBack} variant="outline" color="secondary">
          Back
        </Button>
      </div>

      <div className="grid gap-4 p-6 border rounded-lg">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-gray-600">Amount</p>
            <p className="font-medium">${invoice.amount}</p>
          </div>
          <div>
            <p className="text-gray-600">Status</p>
            <p className="font-medium">{invoice.status}</p>
          </div>
          <div>
            <p className="text-gray-600">Created On</p>
            <p className="font-medium">
              {new Date(invoice.createdOn).toLocaleDateString()}
            </p>
          </div>
        </div>

        {invoice.status !== 'PAID' && (
          <Button
            onClick={onPay}
            disabled={isPaying}
            className="mt-4"
            color="success"
          >
            {isPaying ? 'Processing Payment...' : 'Pay Invoice'}
          </Button>
        )}
      </div>
    </div>
  );
} 