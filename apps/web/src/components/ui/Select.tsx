interface SelectOption {
	value: string;
	label: string;
}

interface SelectProps {
	label: string;
	name: string;
	value?: string;
	onChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
	error?: string;
	options: SelectOption[];
	required?: boolean;
	className?: string;
	disabled?: boolean;
}

export const Select = ({
	label,
	name,
	value,
	onChange,
	error,
	options,
	required = false,
	className,
	disabled = false,
}: SelectProps) => {
	return (
		<div className={className}>
			<label className="block text-sm font-medium leading-6 text-likeme-text">
				{label}
				{required && <span className="text-red-500">*</span>}
			</label>
			<select
				name={name}
				value={value}
				onChange={onChange}
				disabled={disabled}
				className="mt-2 block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
			>
				{options.map((option) => (
					<option key={option.value} value={option.value}>
						{option.label}
					</option>
				))}
			</select>
			{error && <p className="mt-1 text-sm text-red-500">{error}</p>}
		</div>
	);
};
