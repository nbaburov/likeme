/* eslint-disable @typescript-eslint/no-explicit-any */
interface InputProps {
	label: string;
	name: string;
	value?: string | number;
	onChange?: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	error?: string;
	type?:
		| "text"
		| "email"
		| "password"
		| "tel"
		| "textarea"
		| "number"
		| "date"
		| "time"
		| "url";
	required?: boolean;
	placeholder?: string;
	disabled?: boolean;
	min?: number;
	max?: number;
	pattern?: string;
	autoComplete?: string;
	className?: string;
	rows?: number;
}

export const Input = ({
	label,
	name,
	value,
	onChange,
	error,
	type = "text",
	required = false,
	placeholder,
	disabled = false,
	min,
	max,
	pattern,
	autoComplete,
	className = "",
	rows = 3,
}: InputProps) => {
	const baseInputClass = `
		block w-full rounded-md border-0 py-1.5 
		text-likeme-text shadow-sm ring-1 ring-inset 
		ring-gray-300 placeholder:text-gray-400 
		focus:ring-2 focus:ring-inset focus:ring-likeme-primary 
		disabled:cursor-not-allowed disabled:bg-gray-50 
		disabled:text-gray-500 disabled:ring-gray-200
		sm:text-sm sm:leading-6 ${className}
	`;

	return (
		<div>
			<label className="block text-sm font-medium leading-6 text-likeme-text">
				{label}
				{required && <span className="text-red-500 ml-1">*</span>}
			</label>
			<div className="mt-2">
				{type === "textarea" ? (
					<textarea
						name={name}
						value={value}
						onChange={onChange}
						placeholder={placeholder}
						disabled={disabled}
						rows={rows}
						className={baseInputClass}
					/>
				) : (
					<input
						name={name}
						type={type}
						value={value}
						onChange={onChange}
						placeholder={placeholder}
						disabled={disabled}
						min={min}
						max={max}
						pattern={pattern}
						autoComplete={autoComplete}
						className={baseInputClass}
					/>
				)}
				{error && <p className="text-red-500 text-sm mt-1">{error}</p>}
			</div>
		</div>
	);
};
